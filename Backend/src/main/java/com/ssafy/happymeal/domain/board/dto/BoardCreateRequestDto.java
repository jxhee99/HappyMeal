package com.ssafy.happymeal.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardCreateRequestDto {
    private String title;
    private Integer categoryId; // 게시판 카테고리 ID (nullable)
    private List<BlockCreateRequestDto> blocks;
}