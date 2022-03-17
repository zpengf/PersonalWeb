package com.capture.article.mapper;

import com.capture.pojo.vo.CommentsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentsMapperCustom {

    /**
     * 查询文章评论
     */
    public List<CommentsVO> queryArticleCommentList(@Param("paramMap") Map<String, Object> map);

}