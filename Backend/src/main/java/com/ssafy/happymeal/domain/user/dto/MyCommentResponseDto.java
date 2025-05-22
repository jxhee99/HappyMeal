package com.ssafy.happymeal.domain.user.dto;

import com.ssafy.happymeal.domain.commonDto.PageAndSortCriteria;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class MyCommentResponseDto {
    private Long commentId;
    private Long boardId;
    private String content;
    private Timestamp createAt;
    private Timestamp updateAt;

}
