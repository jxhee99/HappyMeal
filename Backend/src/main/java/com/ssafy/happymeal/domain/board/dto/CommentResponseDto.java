package com.ssafy.happymeal.domain.board.dto;

import com.ssafy.happymeal.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private Long boardId;
    private Long userId;
    private Long parentCommentId;
    private String nickname;
    private String content;
    private Timestamp createAt;
    private Timestamp updateAt;
    private List<CommentResponseDto> replies = new ArrayList<>(); // 대댓글을 담을 리스트
//    private long childReplyCount = 0; // 이 댓글의 총 직계 자식 수를 알려주면, 1단계 대댓글 DTO도 자신이 대댓글을 더 가지고 있는지 표시 가능


}
