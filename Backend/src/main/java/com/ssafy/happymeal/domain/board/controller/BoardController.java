package com.ssafy.happymeal.domain.board.controller;

import com.ssafy.happymeal.domain.board.dto.*;
import com.ssafy.happymeal.domain.board.service.BoardService;
import com.ssafy.happymeal.domain.comment.entity.Comment;
import com.ssafy.happymeal.domain.commonDto.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;



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

    @PostMapping
    @PreAuthorize("isAuthenticated()") // 인증된 사용자만 접근 가능
    public ResponseEntity<?> createBoard(
            @Valid @RequestBody BoardCreateRequestDto requestDto,
            @Parameter(hidden = true) // API 문서에서 숨김 처리 (자동 주입)
            @AuthenticationPrincipal UserDetails userDetails) {

        // @PreAuthorize("isAuthenticated()")가 있어서 userDetails는 null이 아니라고 가정 가능.
        // userDetails.getUsername()도 JwtTokenProvider에서 userId를 문자열로 설정했으므로 null이 아님을 기대.
        Long userId;
        try {
            userId = Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            log.error("사용자 ID 파싱 오류: username='{}'은 유효한 Long 타입이 아닙니다. 토큰 생성 로직 확인 필요.", userDetails.getUsername(), e);
            // 이 경우는 토큰에 저장된 subject가 Long으로 변환 불가능한 심각한 문제일 수 있음.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Invalid User ID in Token", "message", "토큰의 사용자 ID 형식이 잘못되었습니다."));
        }

        log.info("게시글 작성 요청 수신 - userId: {}, title: {}", userId, requestDto.getTitle());
        BoardDetailResponseDto createdBoardResponse = boardService.createBoardWithBlocks(requestDto, userId);
        log.info("게시글 생성 완료 - boardId: {}", createdBoardResponse.getBoardId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoardResponse);
    }

    @GetMapping("/{boardId}")
    // 이 API의 접근 권한은 SecurityConfig에서 URL 패턴으로 설정하거나, 여기서 @PreAuthorize로 명시 가능
    // 예: @PreAuthorize("permitAll()") 또는 @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BoardDetailResponseDto> getBoardDetail(
            @PathVariable Long boardId, // @Parameter 설명 생략
            @Parameter(hidden = true) // API 문서에서 숨김 처리 (자동 주입, 선택적)
            @AuthenticationPrincipal UserDetails userDetails) {

        Long currentUserId = null;
        if (userDetails != null && userDetails.getUsername() != null) {
            try {
                currentUserId = Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                // 비로그인 사용자도 접근 가능(required=false)하므로, 파싱 오류는 경고만 남기고 무시
                log.warn("상세 조회 시 현재 사용자 ID 파싱 오류 (무시하고 진행): username='{}'", userDetails.getUsername(), e);
            }
        }
        log.info("게시글 상세 조회 요청 수신 - boardId: {}, 요청자 userId (있다면): {}", boardId, currentUserId);

        BoardDetailResponseDto boardDetailResponse = boardService.getBoardDetailById(boardId);
        log.info("게시글 상세 조회 완료 - boardId: {}", boardDetailResponse.getBoardId());

        return ResponseEntity.ok(boardDetailResponse);
    }

    /* 댓글/대댓글 작성(Create)
    * POST api/boards/{boardId}/comments
    * 접근 권한 : USER */
    @PostMapping("/{boardId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long boardId,
            @Valid @RequestBody CommentRequestDto requestDto) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("댓글 작성 요청 수신 - userId: {}, boardId: {}", userId, boardId);
        Comment createComment = boardService.createComment(userId, boardId, requestDto);
        return ResponseEntity.ok(createComment);

    }

    /* 게시글 내의 댓글/대댓글 조회
     * GET api/boards/{boardId}/comments
     * 접근 권한 : ALL */
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getBoardComments(@PathVariable Long boardId) {
        log.info("댓글 조회 요청 게시글 boardId: {}", boardId);
        List<CommentResponseDto> comments = boardService.getBoardComments(boardId);
        if(comments == null || comments.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // 비어있는 리스트 반환
        }
        return ResponseEntity.ok(comments);
    }

    /* 게시글 수정
     * PUT api/boards/{boardId}
     * 접근 권한: USER */
    @PutMapping("/{boardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateRequestDto requestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId;
        try {
            userId = Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            log.error("사용자 ID 파싱 오류: username='{}'은 유효한 Long 타입이 아닙니다.", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Invalid User ID in Token", "message", "토큰의 사용자 ID 형식이 잘못되었습니다."));
        }

        log.info("게시글 수정 요청 수신 - boardId: {}, userId: {}, title: {}", boardId, userId, requestDto.getTitle());
        
        try {
            BoardDetailResponseDto updatedBoard = boardService.updateBoard(userId, boardId, requestDto);
            log.info("게시글 수정 완료 - boardId: {}", boardId);
            return ResponseEntity.ok(updatedBoard);
        } catch (EntityNotFoundException e) {
            log.warn("게시글 수정 실패 - 게시글을 찾을 수 없음: boardId={}", boardId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("게시글 수정 실패 - 권한 없음: boardId={}, userId={}", boardId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: boardId={}, userId={}", boardId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "서버 내부 처리 중 오류가 발생했습니다."));
        }
    }

    /* 게시글 삭제
     * DELETE api/boards/{boardId}
     * 접근 권한: USER */
    @DeleteMapping("/{boardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteBoard(
            @PathVariable Long boardId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId;
        try {
            userId = Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            log.error("사용자 ID 파싱 오류: username='{}'은 유효한 Long 타입이 아닙니다.", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Invalid User ID in Token", "message", "토큰의 사용자 ID 형식이 잘못되었습니다."));
        }

        log.info("게시글 삭제 요청 수신 - boardId: {}, userId: {}", boardId, userId);
        
        try {
            boardService.deleteBoard(userId, boardId);
            log.info("게시글 삭제 완료 - boardId: {}", boardId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            log.warn("게시글 삭제 실패 - 게시글을 찾을 수 없음: boardId={}", boardId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("게시글 삭제 실패 - 권한 없음: boardId={}, userId={}", boardId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생: boardId={}, userId={}", boardId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "서버 내부 처리 중 오류가 발생했습니다."));
        }
    }

    /* 좋아요 토글
     * POST api/boards/{boardId}/like
     * 접근 권한: USER */
    @PostMapping("/{boardId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BoardLikeResponseDto> toggleLike(
            @PathVariable Long boardId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("좋아요 토글 요청 수신 - boardId: {}, userId: {}", boardId, userId);
        
        BoardLikeResponseDto response = boardService.toggleLike(userId, boardId);
        return ResponseEntity.ok(response);
    }

    /* 좋아요 상태 조회
     * GET api/boards/{boardId}/like
     * 접근 권한: ALL */
    @GetMapping("/{boardId}/like")
    public ResponseEntity<BoardLikeResponseDto> getLikeStatus(
            @PathVariable Long boardId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = null;
        if (userDetails != null) {
            try {
                userId = Long.parseLong(userDetails.getUsername());
            } catch (NumberFormatException e) {
                log.warn("좋아요 상태 조회 시 사용자 ID 파싱 오류 (무시하고 진행): username='{}'", userDetails.getUsername(), e);
            }
        }

        log.info("좋아요 상태 조회 요청 수신 - boardId: {}, userId: {}", boardId, userId);
        
        BoardLikeResponseDto response = boardService.getLikeStatus(userId, boardId);
        return ResponseEntity.ok(response);
    }

    /* 사용자가 좋아요한 게시글 목록 조회
     * GET api/boards/liked
     * 접근 권한: USER */
    @GetMapping("/liked")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<BoardResponseDto>> getLikedBoards(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("좋아요한 게시글 목록 조회 요청 수신 - userId: {}", userId);

        Page<BoardResponseDto> likedBoardsPage = boardService.getLikedBoardsByUser(userId, page, size);
        
        PageResponse<BoardResponseDto> response = new PageResponse<>(
                likedBoardsPage.getContent(),
                likedBoardsPage.getNumber(),
                likedBoardsPage.getSize(),
                likedBoardsPage.getTotalElements()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardId}/likes/count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Long boardId) {
        log.info("좋아요 수 조회 요청 - boardId: {}", boardId);
        int likesCount = boardService.getLikesCount(boardId);
        return ResponseEntity.ok(likesCount);
    }

}
