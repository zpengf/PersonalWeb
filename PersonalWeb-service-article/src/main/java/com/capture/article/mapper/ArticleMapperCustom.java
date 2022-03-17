package com.capture.article.mapper;

import com.capture.my.mapper.MyMapper;
import com.capture.pojo.Article;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapperCustom extends MyMapper<Article> {

    public void updateAppointToPublish();

}