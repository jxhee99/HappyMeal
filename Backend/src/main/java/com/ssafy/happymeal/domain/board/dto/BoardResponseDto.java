package com.ssafy.happymeal.domain.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class BoardResponseDto {
    private Long boardId;
    private Long userId;
    private String nickName;
    private Integer categoryId;
    private String title;
    private Timestamp createAt;
    private Timestamp updateAt;
    private int views; // 조회수
    private int likesCount; // 좋아요 수
    private int commentsCount; // 댓글 수
    private String content; // 내용
    private String imageUrl; // 이미지
}
