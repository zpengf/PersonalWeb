package com.capture.article.service;

import com.capture.utils.PagedGridResult;

public interface CommentPortalService {

    /**
     * 发表评论
     */
    public void createComment(String articleId,
                              String fatherCommentId,
                              String content,
                              String userId,
                              String nickname,
                              String commentUserFace);

    /**
     * 查询文章评论列表
     */
    public PagedGridResult queryArticleComments(String articleId,
                                                Integer page,
                                                Integer pageSize);

    /**
     * 查询我的评论管理列表
     */
    public PagedGridResult queryWriterCommentsMng(String writerId, Integer page, Integer pageSize);

    /**
     * 删除评论
     */
    public void deleteComment(String writerId, String commentId);

}
