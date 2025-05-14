package com.ssafy.happymeal.domain.board.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Block {
    private Long blockId;         // DB: block_id
    private Long boardId;         // DB: board_id (FK)
    private Integer orderIndex;   // DB: order_index
    private String blockType;     // DB: block_type (예: "text", "image")
    private String contentText;   // DB: content_text (nullable)
    private String imageUrl;      // DB: image_url (nullable)
    private String imageCaption;  // DB: image_caption (nullable)
    private LocalDateTime createdAt; // DB: create_at
    private LocalDateTime updatedAt; // DB: update_at
}