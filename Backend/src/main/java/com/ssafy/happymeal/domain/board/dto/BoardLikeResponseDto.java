package com.ssafy.happymeal.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardLikeResponseDto {
    private Long boardId;
    private Long userId;
    private boolean isLiked;
    private int likesCount;
} 