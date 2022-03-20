package com.capture.api.service;

import com.github.pagehelper.PageInfo;
import com.capture.utils.PagedGridResult;
import com.capture.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class BaseService {

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";

    public static final String REDIS_WRITER_FANS_COUNTS = "redis_writer_fans_counts";
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";

    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";

    @Autowired
    public RedisOperator redis;

    @Value("${transferInterface.article}")
    public String articleServiceInterface;

    @Value("${transferInterface.user}")
    public String userServiceInterface;

    @Value("${transferInterface.article_html}")
    public String articleHtmlInterface;

    @Value("${transferInterface.files}")
    public String filesServiceInterface;

    @Value("${transferInterface.admin}")
    public String adminServiceInterface;

    public PagedGridResult setterPagedGrid(List<?> list,
                                            Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(list);
        gridResult.setPage(page);
        //总多少页
        gridResult.setRecords(pageList.getTotal());

        gridResult.setTotal(pageList.getPages());
        return gridResult;
    }

}
