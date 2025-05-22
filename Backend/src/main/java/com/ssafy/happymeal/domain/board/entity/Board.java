package com.ssafy.happymeal.domain.board.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    private Long boardId;
    private Long userId;
    private Integer categoryId; // null을 허용하므로 int가 아닌 Integer
    private String title;
    private Timestamp createAt;
    private Timestamp updateAt;
    private int views; // 조회수
    private int likesCount; // 좋아요 수
    private int commentsCount; // 댓글 수
}
