package com.capture.article.service.impl;

import com.capture.api.config.RabbitMQConfig;
import com.capture.api.config.RabbitMQDelayConfig;
import com.capture.grace.result.GraceJSONResult;
import com.capture.pojo.vo.ArticleDetailVO;
import com.capture.utils.DateUtil;
import com.capture.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.capture.api.service.BaseService;
import com.capture.article.mapper.ArticleMapper;
import com.capture.article.mapper.ArticleMapperCustom;
import com.capture.article.service.ArticleService;
import com.capture.enums.ArticleAppointType;
import com.capture.enums.ArticleReviewLevel;
import com.capture.enums.ArticleReviewStatus;
import com.capture.enums.YesOrNo;
import com.capture.exception.GraceException;
import com.capture.grace.result.ResponseStatusEnum;
import com.capture.pojo.Article;
import com.capture.pojo.Category;
import com.capture.pojo.bo.NewArticleBO;
import com.capture.utils.PagedGridResult;
import com.capture.utils.extend.AliTextReviewUtils;
import com.mongodb.client.gridfs.GridFSBucket;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.n3r.idworker.Sid;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleMapperCustom articleMapperCustom;

    @Autowired
    private Sid sid;

    @Autowired
    private AliTextReviewUtils aliTextReviewUtils;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    public RestTemplate restTemplate;

    @Value("${freemarker.html.article}")
    private String articlePath;


    @Transactional
    @Override
    public void createArticle(NewArticleBO newArticleBO, Category category) {

        String articleId = sid.nextShort();

        Article article = new Article();
        BeanUtils.copyProperties(newArticleBO, article);

        article.setId(articleId);
        article.setCategoryId(category.getId());
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        article.setCommentCounts(0);
        article.setReadCounts(0);

        article.setIsDelete(YesOrNo.NO.type);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        if (article.getIsAppoint() == ArticleAppointType.TIMING.type) {
            article.setPublishTime(newArticleBO.getPublishTime());
        } else if (article.getIsAppoint() == ArticleAppointType.IMMEDIATELY.type) {
            article.setPublishTime(new Date());
        }

        int res = articleMapper.insert(article);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }

        // ?????????????????????mq????????????????????????????????????????????????????????????????????????????????????
        if (article.getIsAppoint() == ArticleAppointType.TIMING.type) {

            Date endDate = newArticleBO.getPublishTime();
            Date startDate = new Date();

            System.out.println(DateUtil.timeBetween(startDate, endDate));
            int delayTimes = (int)(endDate.getTime() - startDate.getTime());

            //??????????????????????????????
            if(delayTimes < 0){
                GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR_TIME);
            }
            MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    // ?????????????????????
                    message.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    // ????????????????????????????????????ms??????
                    message.getMessageProperties()
                            .setDelay(delayTimes);
                    return message;
                }
            };

            //??????????????????
            rabbitTemplate.convertAndSend(
                    RabbitMQDelayConfig.EXCHANGE_DELAY,
                    "publish.delay.display",
                    articleId,
                    messagePostProcessor);

            System.out.println("????????????-?????????????????????" + new Date());
        }


        /**
         * FIXME: ?????????????????????????????????????????????????????????????????????
         */
        // ??????????????????AI??????????????????????????????????????????????????????
//        String reviewTextResult = aliTextReviewUtils.reviewTextContent(newArticleBO.getContent());

        //????????????????????????????????????
        String reviewTextResult = ArticleReviewLevel.REVIEW.type;

        if (reviewTextResult
                .equalsIgnoreCase(ArticleReviewLevel.PASS.type)) {
            // ???????????????????????????????????????????????????
            this.updateArticleStatus(articleId, ArticleReviewStatus.SUCCESS.type);
        } else if (reviewTextResult
                .equalsIgnoreCase(ArticleReviewLevel.REVIEW.type)) {
            // ?????????????????????????????????????????????????????????
            this.updateArticleStatus(articleId, ArticleReviewStatus.WAITING_MANUAL.type);
        } else if (reviewTextResult
                .equalsIgnoreCase(ArticleReviewLevel.BLOCK.type)) {
            // ??????????????????????????????????????????????????????
            this.updateArticleStatus(articleId, ArticleReviewStatus.FAILED.type);
        }

    }

    @Transactional
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", articleId);

        Article pendingArticle = new Article();
        pendingArticle.setArticleStatus(pendingStatus);

        int res = articleMapper.updateByExampleSelective(pendingArticle, example);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        //????????????????????? ?????????????????????????????????
        if (pendingStatus == ArticleReviewStatus.SUCCESS.type) {
            try {
                /**################################
                 * ???????????? ?????????????????????????????????
                 ################################*/
                //createArticleHTML(articleId);


                /**#######################################################
                 * ??????mongodb ???????????????????????????????????? gridfs?????? ???????????????????????????????????????
                 #########################################################*/
                String articleMongoId = createArticleHTMLToGridFS(articleId);
                // ?????????????????????????????????????????????
                this.updateArticleToGridFS(articleId, articleMongoId);

                //?????????????????????????????????mongodb??????html
                //doDownloadArticleHTML(articleId, articleMongoId);

                /**#######################################################
                 * mq?????????????????????????????? ???????????????mq???????????????????????????????????????????????????????????????
                 #########################################################*/
                doDownloadArticleHTMLByMQ(articleId, articleMongoId);
            } catch (Exception e) {
                e.printStackTrace();
                GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_RABBIT_ERROR);
            }
        }



    }



    @Transactional
    @Override
    public void updateArticleToPublish(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(ArticleAppointType.IMMEDIATELY.type);
        articleMapper.updateByPrimaryKeySelective(article);
    }


    @Transactional
    @Override
    public void updateArticleToGridFS(String articleId, String articleMongoId) {
        Article pendingArticle = new Article();
        pendingArticle.setId(articleId);
        pendingArticle.setMongoFileId(articleMongoId);
        int res = articleMapper.updateByPrimaryKeySelective(pendingArticle);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_UPLOAD_ERROR);
        }
    }

    @Transactional
    @Override
    public void updateAppointToPublish() {
        articleMapperCustom.updateAppointToPublish();
    }

    @Override
    public PagedGridResult queryMyArticleList(String userId,
                                              String keyword,
                                              Integer status,
                                              Date startDate,
                                              Date endDate,
                                              Integer page,
                                              Integer pageSize) {

        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("publishUserId", userId);

        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }

        if (status != null && status == 12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }

        criteria.andEqualTo("isDelete", YesOrNo.NO.type);

        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo("publishTime", startDate);
        }
        if (endDate != null) {
            criteria.andLessThanOrEqualTo("publishTime", endDate);
        }

        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryAllArticleListAdmin(Integer status, Integer page, Integer pageSize) {
        Example articleExample = new Example(Article.class);
        articleExample.orderBy("createTime").desc();

        Example.Criteria criteria = articleExample.createCriteria();
        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }

        // ????????????????????????????????????????????????????????????????????????
        if (status != null && status == 12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }

        //isDelete ?????????0
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);

        /**
         * page: ?????????
         * pageSize: ??????????????????
         */
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(articleExample);
        return setterPagedGrid(list, page);
    }

    @Transactional
    @Override
    public void deleteArticle(String userId, String articleId) {
        Example articleExample = makeExampleCriteria(userId, articleId);

        Article pending = new Article();
        pending.setIsDelete(YesOrNo.YES.type);

        int result = articleMapper.updateByExampleSelective(pending, articleExample);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
        }

        deleteHTML(articleId);
    }

    @Transactional
    @Override
    public void withdrawArticle(String userId, String articleId) {
        Example articleExample = makeExampleCriteria(userId, articleId);

        Article pending = new Article();
        pending.setArticleStatus(ArticleReviewStatus.WITHDRAW.type);

        int result = articleMapper.updateByExampleSelective(pending, articleExample);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }

        deleteHTML(articleId);
    }

    /**
     * ??????????????????????????????????????????html
     */
    private void deleteHTML(String articleId) {
        // 1. ???????????????mongoFileId
        Article pending = articleMapper.selectByPrimaryKey(articleId);
        String articleMongoId = pending.getMongoFileId();

        // 2. ??????GridFS????????????
        gridFSBucket.delete(new ObjectId(articleMongoId));

        // 3. ??????????????????HTML??????
//        doDeleteArticleHTML(articleId);
        doDeleteArticleHTMLByMQ(articleId);
    }



    /**
     * ??????HTML ??????????????? ??????????????????????????????
     * @param articleId
     * @throws Exception
     */
    public void createArticleHTML(String articleId) throws Exception {
        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "utf-8");

        // ???????????????????????????
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", detailVO);

        File tempDic = new File(articlePath);
        if (!tempDic.exists()) {
            tempDic.mkdirs();
        }

        String path = articlePath + File.separator + detailVO.getId() + ".html";

        Writer out = new FileWriter(path);
        template.process(map, out);
        out.close();
    }


    /**
     *  ??????html ??????mongodb gridfs???
     * @param articleId
     * @return
     * @throws Exception
     */
    public String createArticleHTMLToGridFS(String articleId) throws Exception {

        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "utf-8");

        // ???????????????????????????  ??????????????????????????????????????????????????????????????????????????????
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", detailVO);

        //???html?????????????????????????????????
        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        InputStream inputStream = IOUtils.toInputStream(htmlContent);

        //?????????gridfs
        ObjectId fileId = gridFSBucket.uploadFromStream(detailVO.getId() + ".html",inputStream);
        return fileId.toString();
    }


    /**
     * ??????article-html??????????????? ???mongodb????????????html??????
     * ????????????????????????????????????
     * @param articleId
     * @param articleMongoId
     */
    private void doDownloadArticleHTML(String articleId, String articleMongoId) {


//        String url = articleHtmlInterface + "article/html/download?articleId="
//                + articleId +
//                "&articleMongoId="
//                + articleMongoId;
        String serviceId = "service-article-html";
        String url
                = "http://" + serviceId + "/article/html/download?articleId=" + articleId+
                "&articleMongoId="
                + articleMongoId;;
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        int status = responseEntity.getBody();
        if (status != HttpStatus.OK.value()) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
    }



    private void doDownloadArticleHTMLByMQ(String articleId, String articleMongoId) {


        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.download.do",
                articleId + "," + articleMongoId);

    }


    /**
     * ??????????????????rest???????????????????????????
     * @param articleId
     * @return
     */
    public ArticleDetailVO getArticleDetail(String articleId) {
//        String url
//                = articleInterface + "portal/article/detail?articleId=" + articleId;

        String serviceId = "SERVICE-ARTICLE";
        String url
                = "http://" + serviceId + "/portal/article/detail?articleId=" + articleId;



        ResponseEntity<GraceJSONResult> responseEntity
                = restTemplate.getForEntity(url, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        ArticleDetailVO detailVO = null;
        if (bodyResult.getStatus() == 200) {
            String detailJson = JsonUtils.objectToJson(bodyResult.getData());
            detailVO = JsonUtils.jsonToPojo(detailJson, ArticleDetailVO.class);
        }
        return detailVO;
    }


    private Example makeExampleCriteria(String userId, String articleId) {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo("publishUserId", userId);
        criteria.andEqualTo("id", articleId);
        return articleExample;
    }


    private void doDeleteArticleHTML(String articleId) {
//        String url = articleHtmlInterface + "article/html/delete?articleId=" + articleId;

        String serviceId = "service-article-html";
        String url
                = "http://" + serviceId + "/article/html/delete?articleId=" + articleId;

        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        int status = responseEntity.getBody();
        if (status != HttpStatus.OK.value()) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }


    private void doDeleteArticleHTMLByMQ(String articleId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.html.delete.do", articleId);
    }

}
