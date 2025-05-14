package com.ssafy.happymeal.domain.board.dto;

import lombok.Getter;

/* 작성자 검색용 */
@Getter
public class BoardAuthorSearchCriteria extends PageAndSortCriteria {
    private String nickname; // 작성자 닉네임

    public BoardAuthorSearchCriteria(String nickname, String sortBy, int page, int size) {
        super(sortBy, page, size);
        this.nickname = nickname;
    }
}
