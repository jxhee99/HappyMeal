package com.ssafy.happymeal.domain.board.service;

import com.ssafy.happymeal.domain.board.dao.BoardDAO;
import com.ssafy.happymeal.domain.board.dto.BoardAuthorSearchCriteria;
import com.ssafy.happymeal.domain.board.dto.BoardCategoryCriteria;
import com.ssafy.happymeal.domain.board.dto.BoardResponseDto;
import com.ssafy.happymeal.domain.board.dto.BoardTitleSearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardDAO boardDAO;

    // 게시글 조회(필터링, 정렬, 페이징 포함)
    @Override
    public Page<BoardResponseDto> getBoardsByCategory(BoardCategoryCriteria criteria) {
        // 1. DAO에게 요청사항인 criteria 전달해서 조건에 맞는 게시글 목록(페이징 처리된)을 가져옴
        List<BoardResponseDto> boards = boardDAO.findBoardByCategory(criteria);

        // 2. 동일한 필터 조건으로 전체 게시글 수 조회
        long totalElements = boardDAO.countBoardsByCategory(criteria);

        // 3. 가져온 목록과 전체 개수, 현재 페이지 정보를 합쳐서 Page 객체로 감싸서 반환
        // PageRequest.of(page, size)에서 page는 0부터 시작하는 인덱스
        return new PageImpl<>(boards, PageRequest.of(criteria.getPage(), criteria.getSize()), totalElements);
    }

    // 게시글 검색 (제목으로 검색)
    @Override
    public Page<BoardResponseDto> searchBoardsByTitle(BoardTitleSearchCriteria criteria) {
        List<BoardResponseDto> boards = boardDAO.searchBoardsByTitle(criteria);
        long totalElements = boardDAO.countBoardsByTitle(criteria);
        return new PageImpl<>(boards, PageRequest.of(criteria.getPage(), criteria.getSize()), totalElements);
    }

    // 게시글 검색 (제목으로 검색)
    @Override
    public Page<BoardResponseDto> searchBoardsByAuthor(BoardAuthorSearchCriteria criteria) {
        List<BoardResponseDto> boards = boardDAO.searchBoardsByAuthor(criteria);
        long totalElements = boardDAO.countBoardsByAuthor(criteria);
        return new PageImpl<>(boards, PageRequest.of(criteria.getPage(), criteria.getSize()), totalElements);
    }


}
