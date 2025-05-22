package com.ssafy.happymeal.domain.comment.service;

public interface CommentService {
    void deleteComment(Long userId, Long boardId, Long commentId);
}
