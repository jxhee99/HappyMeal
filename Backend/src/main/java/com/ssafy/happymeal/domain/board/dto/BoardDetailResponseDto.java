package com.ssafy.happymeal.domain.board.dto;

import com.ssafy.happymeal.domain.board.entity.Block;
import com.ssafy.happymeal.domain.board.entity.Board;
import com.ssafy.happymeal.domain.user.entity.User; // User 엔티티 임포트
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp; // Board 엔티티와 타입 일치
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BoardDetailResponseDto {
    private Long boardId;
    private Long userId;
    private String nickname;            // 작성자 닉네임
    private String userProfileImgUrl;   // 작성자 프로필 이미지 URL
    private Integer categoryId;         // Board 엔티티가 int면 Integer로 자동 변환 (혹은 Integer로 통일)
    private String title;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer views;              // Board 엔티티가 int면 Integer로 자동 변환
    private Integer likesCount;
    private Integer commentsCount;
    private List<BlockResponseDto> blocks;

    public static BoardDetailResponseDto fromEntities(Board board, User author, List<Block> blockEntities) {
        if (board == null) return null;

        List<BlockResponseDto> blockDtos = blockEntities.stream()
                .map(BlockResponseDto::fromEntity)
                .collect(Collectors.toList());

        String authorNickname = "알 수 없는 사용자"; // 기본값
        String authorProfileImgUrl = null;      // 기본값
        if (author != null) {
            authorNickname = author.getNickname();
            authorProfileImgUrl = author.getProfileImageUrl();
        }

        return BoardDetailResponseDto.builder()
                .boardId(board.getBoardId())
                .userId(board.getUserId())
                .nickname(authorNickname)
                .userProfileImgUrl(authorProfileImgUrl)
                .categoryId(board.getCategoryId()) // int -> Integer 자동 변환
                .title(board.getTitle())
                .createdAt(board.getCreateAt()) // 필드명 일치
                .updatedAt(board.getUpdateAt()) // 필드명 일치
                .views(board.getViews())       // int -> Integer 자동 변환
                .likesCount(board.getLikesCount())
                .commentsCount(board.getCommentsCount())
                .blocks(blockDtos)
                .build();
    }
}