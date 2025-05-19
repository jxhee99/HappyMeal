package com.ssafy.happymeal.domain.user.dto;

import com.ssafy.happymeal.domain.commonDto.PageAndSortCriteria;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class MyBoardResponseDto{
    private Long boardId;
    private Integer categoryId;
    private String title;
    private Timestamp createAt;
    private Timestamp updateAt;
    private int views;
    private int likesCount;
    private int comments_count;
}
