package com.capture.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.capture.api.service.BaseService;
import com.capture.article.mapper.ArticleMapper;
import com.capture.article.mapper.ArticleMapperCustom;
import com.capture.article.service.ArticlePortalService;
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
import com.capture.pojo.vo.ArticleDetailVO;
import com.capture.utils.PagedGridResult;
import com.capture.utils.extend.AliTextReviewUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ArticlePortalServiceImpl extends BaseService implements ArticlePortalService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PagedGridResult queryIndexArticleList(String keyword,
                                                 Integer category,
                                                 Integer page,
                                                 Integer pageSize) {

        Example articleExample = new Example(Article.class);
        articleExample.orderBy("publishTime").desc();
        Example.Criteria criteria = articleExample.createCriteria();

        /**
         * 查询首页文章的自带隐性查询条件：
         * isAppoint=即使发布，表示文章已经直接发布的，或者定时任务到点发布的
         * isDelete=未删除，表示文章只能够显示未删除
         * articleStatus=审核通过，表示只有文章经过机审/人工审核之后才能展示
         */
        criteria.andEqualTo("isAppoint", YesOrNo.NO.type);
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);

        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }
        if (category != null) {
            criteria.andEqualTo("categoryId", category);
        }

        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(articleExample);

        return setterPagedGrid(list, page);
    }

    @Override
    public List<Article> queryHotList() {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = setDefualArticleExample(articleExample);

        PageHelper.startPage(1, 5);
        List<Article> list  = articleMapper.selectByExample(articleExample);
        return list;
    }

    private Example.Criteria setDefualArticleExample(Example articleExample) {
        articleExample.orderBy("publishTime").desc();
        Example.Criteria criteria = articleExample.createCriteria();

        /**
         * 查询首页文章的自带隐性查询条件：
         * isAppoint=即使发布，表示文章已经直接发布的，或者定时任务到点发布的
         * isDelete=未删除，表示文章只能够显示未删除
         * articleStatus=审核通过，表示只有文章经过机审/人工审核之后才能展示
         */
        criteria.andEqualTo("isAppoint", YesOrNo.NO.type);
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);

        return criteria;
    }

    @Override
    public PagedGridResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        Example articleExample = new Example(Article.class);

        Example.Criteria criteria = setDefualArticleExample(articleExample);
        criteria.andEqualTo("publishUserId", writerId);

        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(articleExample);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryGoodArticleListOfWriter(String writerId) {
        Example articleExample = new Example(Article.class);
        articleExample.orderBy("publishTime").desc();

        Example.Criteria criteria = setDefualArticleExample(articleExample);
        criteria.andEqualTo("publishUserId", writerId);

        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(1, 5);
        List<Article> list = articleMapper.selectByExample(articleExample);
        return setterPagedGrid(list, 1);
    }

    @Override
    public ArticleDetailVO queryDetail(String articleId) {

        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(YesOrNo.NO.type);
        article.setIsDelete(YesOrNo.NO.type);
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);

        Article result = articleMapper.selectOne(article);

        ArticleDetailVO detailVO = new ArticleDetailVO();
        BeanUtils.copyProperties(result, detailVO);

        detailVO.setCover(result.getArticleCover());

        return detailVO;
    }
}
