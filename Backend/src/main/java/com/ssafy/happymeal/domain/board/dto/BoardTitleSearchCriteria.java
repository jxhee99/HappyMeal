package com.ssafy.happymeal.domain.board.dto;

import com.ssafy.happymeal.domain.commonDto.PageAndSortCriteria;
import lombok.Getter;

/* 제목 검색용 */
@Getter
public class BoardTitleSearchCriteria extends PageAndSortCriteria {
    private String title;


    public BoardTitleSearchCriteria(String title, String sortBy, int page, int size) {
        super(sortBy, page, size);
        this.title = title;
    }
}
