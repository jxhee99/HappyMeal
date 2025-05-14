package com.ssafy.happymeal.domain.board.dto;

import com.ssafy.happymeal.domain.board.entity.Block;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BlockResponseDto {
    private Long blockId;
    private Integer orderIndex;
    private String blockType;
    private String contentText;
    private String imageUrl;
    private String imageCaption;

    public static BlockResponseDto fromEntity(Block block) {
        if (block == null) return null;
        return BlockResponseDto.builder()
                .blockId(block.getBlockId())
                .orderIndex(block.getOrderIndex())
                .blockType(block.getBlockType())
                .contentText(block.getContentText())
                .imageUrl(block.getImageUrl())
                .imageCaption(block.getImageCaption())
                .build();
    }
}