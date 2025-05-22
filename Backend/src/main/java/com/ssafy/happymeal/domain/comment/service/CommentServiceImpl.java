package com.ssafy.happymeal.domain.comment.service;

import com.ssafy.happymeal.domain.comment.dao.CommentDAO;
import com.ssafy.happymeal.domain.comment.entity.Comment;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    
    private final CommentDAO commentDAO;

    @Override
    @Transactional
    public void deleteComment(Long userId, Long boardId, Long commentId) {
        // 댓글 존재 여부 및 권한 확인
        Comment comment = commentDAO.findCommentById(commentId, boardId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        // 댓글 작성자 확인
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalStateException("댓글을 삭제할 권한이 없습니다.");
        }

        // 댓글 삭제 (CASCADE 설정으로 인해 자식 댓글도 자동 삭제됨)
        int childCommentCount = commentDAO.countChildComments(commentId);
        commentDAO.deleteComment(commentId);
        commentDAO.deleteCommentCount(boardId, childCommentCount);
        log.info("댓글 삭제 완료 - commentId: {}", commentId);


    }
}
