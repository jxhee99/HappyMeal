package com.ssafy.happymeal.domain.board.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardLike {
    private Long userId;
    private Long boardId;
    private LocalDateTime createAt;
} 