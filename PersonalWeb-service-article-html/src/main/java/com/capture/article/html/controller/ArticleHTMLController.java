package com.capture.article.html.controller;

import com.capture.api.controller.article.ArticleHTMLControllerApi;
import com.capture.grace.result.GraceJSONResult;
import com.capture.grace.result.ResponseStatusEnum;
import com.mongodb.client.gridfs.GridFSBucket;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@RestController

//作为某个服务的服务端如果访问这个controller出错 当前服务的端口死掉 就全局降级 防止微服务出现雪崩
@DefaultProperties(defaultFallback = "defaultFallback")
public class ArticleHTMLController implements ArticleHTMLControllerApi {

    final static Logger logger = LoggerFactory.getLogger(ArticleHTMLController.class);

    @Autowired
    private GridFSBucket gridFSBucket;

    @Value("${freemarker.html.article}")
    private String articlePath;


    public GraceJSONResult defaultFallback() {
        logger.error("ArticleHTMLController进入全局降级");
        System.out.println("ArticleHTMLController进入全局降级");
        return GraceJSONResult.ok(ResponseStatusEnum.SYSTEM_ERROR_FILES_SERVICE);
    }


    @Override
    public Integer download(String articleId, String articleMongoId)
            throws Exception {

        // 拼接最终文件的保存的地址
        String path = articlePath + File.separator + articleId + ".html";

        // 获取文件流，定义存放的位置和名称
        File file = new File(path);
        // 创建输出流
        OutputStream outputStream = new FileOutputStream(file);
        // 执行下载
        gridFSBucket.downloadToStream(new ObjectId(articleMongoId), outputStream);

        return HttpStatus.OK.value();
    }

    @Override
    public Integer delete(String articleId) throws Exception {

        // 拼接最终文件的保存的地址
        String path = articlePath + File.separator + articleId + ".html";

        // 获取文件流，定义存放的位置和名称
        File file = new File(path);

        // 删除文件
        file.delete();

        return HttpStatus.OK.value();
    }
}
