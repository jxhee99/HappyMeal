package com.ssafy.happymeal.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
    private Long parentCommentId; // 없으면 null
}
