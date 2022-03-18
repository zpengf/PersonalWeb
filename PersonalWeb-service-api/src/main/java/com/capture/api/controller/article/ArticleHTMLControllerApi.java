package com.capture.api.controller.article;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;


@Api(value = "静态化文章业务的controller", tags = {"静态化文章业务的controller"})
@RequestMapping("article/html")
public interface ArticleHTMLControllerApi {

    @GetMapping("download")
    @ApiOperation(value = "下载html", notes = "下载html", httpMethod = "GET")
    public Integer download(String articleId, String articleMongoId) throws Exception;

    @GetMapping("delete")
    @ApiOperation(value = "删除html", notes = "删除html", httpMethod = "GET")
    public Integer delete(String articleId) throws Exception;
}
