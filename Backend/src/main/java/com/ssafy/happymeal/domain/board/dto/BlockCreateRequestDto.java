package com.ssafy.happymeal.domain.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // Controller에서 @RequestBody로 받을 때 Jackson이 사용
@NoArgsConstructor
public class BlockCreateRequestDto {
    private Integer orderIndex;
    private String blockType;     // "text", "image", "video" 등
    private String contentText;   // blockType="text" 일 때
    private String imageUrl;      // blockType="image" 또는 "video" 일 때
    private String imageCaption;  // blockType="image" 일 때
}