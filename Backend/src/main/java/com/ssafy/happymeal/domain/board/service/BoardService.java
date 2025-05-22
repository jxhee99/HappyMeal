package com.ssafy.happymeal.domain.board.service;

import com.ssafy.happymeal.domain.board.dto.*;
import com.ssafy.happymeal.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {
    // 게시글 조회(필터링, 정렬, 페이징 포함)
    Page<BoardResponseDto> getBoardsByCategory(BoardCategoryCriteria criteria);

    // 게시글 검색 (제목으로 검색)
    Page<BoardResponseDto> searchBoardsByTitle(BoardTitleSearchCriteria criteria);

    // 게시글 검색 (작성자로 검색)
    Page<BoardResponseDto> searchBoardsByAuthor(BoardAuthorSearchCriteria criteria);

    // === 신규 기능: 게시글 생성 (블록 포함) ===
    BoardDetailResponseDto createBoardWithBlocks(BoardCreateRequestDto requestDto, Long userId);

    // === 신규 기능: 게시글 상세 조회 (블록 및 작성자 정보 포함) ===
    BoardDetailResponseDto getBoardDetailById(Long boardId);

    // 댓글/대댓글 생성
    Comment createComment(Long userId, Long boardId, CommentRequestDto requestDto);

    // 게시글의 댓글/대댓글(1개) 조회
    List<CommentResponseDto> getBoardComments(Long boardId);

    // 게시글 수정
    BoardDetailResponseDto updateBoard(Long userId, Long boardId, BoardUpdateRequestDto requestDto);

    // 게시글 삭제
    void deleteBoard(Long userId, Long boardId);

    // 좋아요 토글
    BoardLikeResponseDto toggleLike(Long userId, Long boardId);

    // 좋아요 상태 조회
    BoardLikeResponseDto getLikeStatus(Long userId, Long boardId);

    // 사용자가 좋아요한 게시글 목록 조회
    Page<BoardResponseDto> getLikedBoardsByUser(Long userId, int page, int size);
}
