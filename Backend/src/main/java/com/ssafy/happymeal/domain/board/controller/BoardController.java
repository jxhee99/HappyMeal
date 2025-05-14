package com.ssafy.happymeal.domain.board.controller;

import com.ssafy.happymeal.domain.board.dto.BoardAuthorSearchCriteria;
import com.ssafy.happymeal.domain.board.dto.BoardCategoryCriteria;
import com.ssafy.happymeal.domain.board.dto.BoardResponseDto;
import com.ssafy.happymeal.domain.board.dto.BoardTitleSearchCriteria;
import com.ssafy.happymeal.domain.board.service.BoardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

// 가상의 PageResponse (페이징된 결과를 담는 클래스)
@Getter
@Setter
class PageResponse<T> {
    public List<T> content;
    public int pageNumber;
    public int pageSize;
    public long totalElements;
    public int totalPages;

    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize); // 한 페이지에 몇개 보여줄지에 따라 전체 페이지 수 계산
    }

}

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /* 게시글 조회(필터링, 정렬, 페이징 포함)
     * GET api/boards
     * GET api/boards?categoryId={categoryId}&sortBy={정렬기준}&page={}&size={}
     *  접근 권한: ALL */
    @GetMapping
    public ResponseEntity<PageResponse<BoardResponseDto>> getBoardsByCategory(
            @RequestParam(required = false) Long categoryId, // required = false ; 필수 파라미터 ❌
            // defaultValue : 파라미터가 없을 경우 기본값
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 1. 요청 파라미터를 BoardSearchCriteria 바구니에 담음
        BoardCategoryCriteria criteria = new BoardCategoryCriteria(categoryId, sortBy, page, size);

        // 2. 서비스 계층에 바구니를 전달하고 게시글 목록과 전체 개수 정보를 요청
        Page<BoardResponseDto> boardPage = boardService.getBoardsByCategory(criteria);

        // 3. 서비스로부터 받은 Page 객체를 정의한 PageResponse로 변환해서 응답
        PageResponse<BoardResponseDto> response = new PageResponse<>(
                boardPage.getContent(),
                boardPage.getNumber(),
                boardPage.getSize(),
                boardPage.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    /* 게시글 검색 ("제목"으로 검색)
    * GET api/boards/search/title?title={제목}
    *  접근 권한: ALL */
    @GetMapping("/search/title")
    public ResponseEntity<PageResponse<BoardResponseDto>> searchBoardsByTitle(
            @RequestParam String title,
            // defaultValue : 파라미터가 없을 경우 기본값
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("검색 요청 제목 : title={}",title);
        BoardTitleSearchCriteria criteria = new BoardTitleSearchCriteria(title, sortBy, page, size);

        Page<BoardResponseDto> boardPage = boardService.searchBoardsByTitle(criteria);

        if(boardPage.getContent().isEmpty()) {
            log.info("제목과 일치하는 게시글 없음 : title={}", title);
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        PageResponse<BoardResponseDto> response = new PageResponse<>(
                boardPage.getContent(),
                boardPage.getNumber(),
                boardPage.getSize(),
                boardPage.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    /* 게시글 검색 ("닉네임"으로 검색)
     * GET api/boards/search/author?nickname={닉네임}
     *  접근 권한: ALL */
    @GetMapping("/search/author")
    public ResponseEntity<PageResponse<BoardResponseDto>> searchBoardsByAuthor(
            @RequestParam String nickname,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("검색 요청 닉네임 : nickname={}",nickname);

        BoardAuthorSearchCriteria criteria = new BoardAuthorSearchCriteria(nickname, sortBy, page, size);

        Page<BoardResponseDto> boardPage = boardService.searchBoardsByAuthor(criteria);

        if(boardPage.getContent().isEmpty()) {
            log.info("작성자와 일치하는 게시글 없음 : nickname={}", nickname);
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        PageResponse<BoardResponseDto> response = new PageResponse<>(
                boardPage.getContent(),
                boardPage.getNumber(),
                boardPage.getSize(),
                boardPage.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

}
