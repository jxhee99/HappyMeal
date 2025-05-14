package com.ssafy.happymeal.domain.board.dto;

import lombok.Getter;

/* 카테고리 기반 팔터링용
* 카테고리
1. 맛집 추천 및 리뷰
2. 레시피 및 식단 공유
3. 자유게시판
4. 공지 */
@Getter
public class BoardCategoryCriteria extends PageAndSortCriteria {
    private Long categoryId;

    public BoardCategoryCriteria(Long categoryId, String sortBy, int page, int size) {
        super(sortBy, page, size);
        this.categoryId = categoryId;
    }
}
