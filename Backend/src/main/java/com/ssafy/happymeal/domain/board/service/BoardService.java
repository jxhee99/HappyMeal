package com.ssafy.happymeal.domain.board.service;

import com.ssafy.happymeal.domain.board.dto.BoardAuthorSearchCriteria;
import com.ssafy.happymeal.domain.board.dto.BoardCategoryCriteria;
import com.ssafy.happymeal.domain.board.dto.BoardResponseDto;
import com.ssafy.happymeal.domain.board.dto.BoardTitleSearchCriteria;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BoardService {
    // 게시글 조회(필터링, 정렬, 페이징 포함)
    Page<BoardResponseDto> getBoardsByCategory(BoardCategoryCriteria criteria);

    // 게시글 검색 (제목으로 검색)
    Page<BoardResponseDto> searchBoardsByTitle(BoardTitleSearchCriteria criteria);

    // 게시글 검색 (작성자로 검색)
    Page<BoardResponseDto> searchBoardsByAuthor(BoardAuthorSearchCriteria criteria);
}
