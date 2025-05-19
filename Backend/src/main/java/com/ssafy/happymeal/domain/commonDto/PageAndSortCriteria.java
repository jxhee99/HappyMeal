package com.ssafy.happymeal.domain.commonDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/* 공통 페이징/정렬 정보 DTO
* 추상 클래스로 만들어 공통적으로 사용하기 위함 */
@Getter
@Setter
@AllArgsConstructor
public abstract class PageAndSortCriteria {
    private String sortBy; // 정렬기준
    private int page; // 요청 페이지 번호
    private  int size; // 한 페이지에 보여줄 게시글 수

    // DB에서 사용할 offset 계산
    public int getOffset() {
        return page * size;
    }

}
