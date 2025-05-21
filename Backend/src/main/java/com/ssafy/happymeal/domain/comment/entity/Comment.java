package com.ssafy.happymeal.domain.comment.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long commentId;
    private Long boardId;
    private Long userId;
    private String content;
    private Long parentCommentId; // 대댓글을 위한 컬럼
    private Timestamp createAt;
    private Timestamp updateAt;

}
