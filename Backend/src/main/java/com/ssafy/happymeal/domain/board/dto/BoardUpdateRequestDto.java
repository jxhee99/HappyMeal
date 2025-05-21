package com.ssafy.happymeal.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardUpdateRequestDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    private List<BlockUpdateRequestDto> blocks;

    @Getter
    @Setter
    public static class BlockUpdateRequestDto {
        private Long blockId;
        private Integer orderIndex;
        private String blockType;
        private String contentText;
        private String imageUrl;
        private String imageCaption;
    }
} 