package com.ssafy.happymeal.domain.comment.controller;

import com.ssafy.happymeal.domain.comment.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @DeleteMapping("/{boardId}/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("댓글 삭제 요청 수신 - userId: {}, boardId: {}, commentId: {}", userId, boardId, commentId);

        try {
            commentService.deleteComment(userId, boardId, commentId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            log.warn("댓글 삭제 실패 - 댓글을 찾을 수 없음: commentId={}", commentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("댓글 삭제 실패 - 권한 없음: commentId={}, userId={}", commentId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생: commentId={}, userId={}", commentId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "서버 내부 처리 중 오류가 발생했습니다."));
        }
    }
}
