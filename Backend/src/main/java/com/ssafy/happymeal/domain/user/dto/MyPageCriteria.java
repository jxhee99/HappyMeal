package com.ssafy.happymeal.domain.user.dto;

import com.ssafy.happymeal.domain.commonDto.PageAndSortCriteria;

public class MyPageCriteria extends PageAndSortCriteria {
    private Long userId;

    public MyPageCriteria(Long userId, String sortBy, int page, int size) {
        super(sortBy, page, size);
        this.userId = userId;
    }
}
