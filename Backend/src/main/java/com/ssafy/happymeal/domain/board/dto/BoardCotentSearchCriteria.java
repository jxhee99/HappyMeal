package com.ssafy.happymeal.domain.board.dto;

import com.ssafy.happymeal.domain.commonDto.PageAndSortCriteria;
import lombok.Getter;

/* 내용 검색용 */
@Getter
public class BoardCotentSearchCriteria extends PageAndSortCriteria {
    private String contentKeyword;

    public BoardCotentSearchCriteria(String contentKeyword, String sortBy, int page, int size) {
        super(sortBy, page, size);
        this.contentKeyword = contentKeyword;
    }
}
