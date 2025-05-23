package com.ssafy.happymeal.domain.board.service;

import com.ssafy.happymeal.domain.board.dao.BlockDAO;
import com.ssafy.happymeal.domain.board.dao.BoardDAO;
import com.ssafy.happymeal.domain.board.dao.BoardLikeDAO;
import com.ssafy.happymeal.domain.board.dto.*;
import com.ssafy.happymeal.domain.board.entity.Block;
import com.ssafy.happymeal.domain.board.entity.Board;
import com.ssafy.happymeal.domain.board.entity.BoardLike;
import com.ssafy.happymeal.domain.comment.dao.CommentDAO;
import com.ssafy.happymeal.domain.comment.entity.Comment;
import com.ssafy.happymeal.domain.user.dao.UserDAO;
import com.ssafy.happymeal.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardDAO boardDAO;
    private final BlockDAO blockDAO;
    private final UserDAO userDao;
    private final CommentDAO commentDAO;
    private final BoardLikeDAO boardLikeDAO;

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

    @Override
    @Transactional // 여러 DB 쓰기 작업이므로 트랜잭션 필수
    public BoardDetailResponseDto createBoardWithBlocks(BoardCreateRequestDto requestDto, Long userId) {
        log.info("게시글 생성 서비스 시작 - userId: {}, title: {}", userId, requestDto.getTitle());

        User author = userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("작성자(User) 정보를 찾을 수 없습니다. ID: " + userId));

        Board board = Board.builder()
                .userId(userId)
                .title(requestDto.getTitle())
                .categoryId(requestDto.getCategoryId() != null ? requestDto.getCategoryId() : 0)
                .views(0)
                .likesCount(0)
                .commentsCount(0)
                .build();
        boardDAO.saveBoard(board);
        log.debug("Board 엔티티 저장 완료 - boardId: {}", board.getBoardId());

        List<Block> savedBlockEntities = new ArrayList<>(); // Block 엔티티 리스트
        if (requestDto.getBlocks() != null && !requestDto.getBlocks().isEmpty()) {
            for (BlockCreateRequestDto blockDto : requestDto.getBlocks()) {
                Block block = Block.builder()
                        .boardId(board.getBoardId())
                        .orderIndex(blockDto.getOrderIndex())
                        .blockType(blockDto.getBlockType())
                        .contentText(blockDto.getContentText())
                        .imageUrl(blockDto.getImageUrl())
                        .imageCaption(blockDto.getImageCaption())
                        .build();
                blockDAO.saveBlock(block);
                savedBlockEntities.add(block); // 저장된 Block 엔티티를 리스트에 추가
                log.debug("Block 엔티티 저장 완료 - blockId: {}, boardId: {}", block.getBlockId(), block.getBoardId());
            }
        }

        Board createdBoard = boardDAO.findBoardById(board.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("방금 생성된 게시글 정보를 찾을 수 없습니다. ID: " + board.getBoardId()));

        // ❗️❗️❗️ 수정된 부분 ❗️❗️❗️
        // BoardDetailResponseDto.fromEntities는 List<Block>을 받아서 내부에서 List<BlockResponseDto>로 변환합니다.
        // 따라서, blockResponseDtos로 미리 변환하지 않고, savedBlockEntities (List<Block>)를 직접 전달합니다.
        log.info("게시글 및 블록 생성 서비스 완료 - boardId: {}", createdBoard.getBoardId());
        return BoardDetailResponseDto.fromEntities(createdBoard, author, savedBlockEntities); // savedBlockEntities 전달
    }

    @Override
    @Transactional // 조회수 증가(UPDATE)가 포함되므로 readOnly=false
    public BoardDetailResponseDto getBoardDetailById(Long boardId) {
        log.info("게시글 상세 조회 서비스 시작 - boardId: {}", boardId);

        // 1. 게시글 조회
        Board board = boardDAO.findBoardById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardId));

        // 2. 조회수 증가
        int updatedRows = boardDAO.incrementViewCount(boardId);
        if (updatedRows == 0) {
            log.warn("게시글(ID: {}) 조회수 증가 실패 또는 해당 게시글이 존재하지 않을 수 있음.", boardId);
        }

        // 3. 작성자 정보 조회
        User author = userDao.findById(board.getUserId())
                .orElse(User.builder().nickname("탈퇴한 사용자").userId(board.getUserId()).build());

        // 4. 블록 정보 조회
        List<Block> blockEntities = blockDAO.findBlocksByBoardId(boardId);

        log.info("게시글 상세 조회 서비스 완료 - boardId: {}", board.getBoardId());
        return BoardDetailResponseDto.fromEntities(board, author, blockEntities);
    }

    // 댓글/대댓글 생성
    @Override
    @Transactional
    public Comment createComment(Long userId, Long boardId, CommentRequestDto requestDto) {
        // 1. 게시글 존재 확인
        Board board = boardDAO.findBoardById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. boardId: " + boardId));

        // 2. 부모댓글(존재시) 요휴성 체크
        if(requestDto.getParentCommentId()!=null) {
           Comment comment =  commentDAO.findCommentById(requestDto.getParentCommentId(), boardId)
                   .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다. parentCommentId : " + requestDto.getParentCommentId()));
        }

        Comment comment = Comment.builder()
                .boardId(boardId)
                .userId(userId)
                .content(requestDto.getContent())
                .parentCommentId(requestDto.getParentCommentId())
                .build();

        int save = commentDAO.saveComment(comment);

        // 데이터베이스  저장 완료
        if(save==1) {
            log.info("댓글 생성 완료 : userId={}, commentId={}", userId, comment.getCommentId());
            if(comment.getCommentId()==null) {
                throw new IllegalArgumentException("commentId 생성 실패");
            }
            commentDAO.updateCommentCount(comment);
            return comment;
        }
        // 저장 실패 시 반환 로직
        throw  new RuntimeException("댓글 저장 실패 userId : " + userId);
    }

    // 게시글의 댓글/대댓글(1개) 조회
    @Override
    public List<CommentResponseDto> getBoardComments(Long boardId) {

        // 1. 최상위 댓글 목록 조회
        List<CommentResponseDto> topLevelCommentElements = commentDAO.findTopLevelCommentsByBoardId(boardId);

        if(topLevelCommentElements.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 최상위 댓글 ID 리스트 추출
        List<Long> topLevelCommentIds = topLevelCommentElements.stream()
                .map(CommentResponseDto::getCommentId)
                .collect(Collectors.toList());

        // 3. 1단계 대댓글 조회
        List<CommentResponseDto> replyElements = Collections.emptyList();
        if(!topLevelCommentIds.isEmpty()) {
            replyElements = commentDAO.findChildRepliesByParentId(topLevelCommentIds);
        }

        // 4. 1단계 대댓글들을 부모 ID 기준으로 그룹핑(Map<부모 ID, List<1단계대댓글>>)
        Map<Long, List<CommentResponseDto>> repliesMap = replyElements.stream()
                .collect(Collectors.groupingBy(CommentResponseDto::getParentCommentId));

        // 5. 최상위 댓글에 1단계 대댓글 연결
        topLevelCommentElements.forEach(element -> {
            element.setReplies(repliesMap.getOrDefault(element.getCommentId(), Collections.emptyList()));
        });
        return topLevelCommentElements;
    }

    @Override
    @Transactional
    public BoardDetailResponseDto updateBoard(Long userId, Long boardId, BoardUpdateRequestDto requestDto) {
        // 1. 게시글 존재 확인 및 작성자 확인
        Board board = boardDAO.findBoardById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. boardId: " + boardId));

        if (!board.getUserId().equals(userId)) {
            throw new IllegalStateException("게시글 수정 권한이 없습니다.");
        }

        // 2. 게시글 기본 정보 업데이트
        board.setTitle(requestDto.getTitle());
        board.setCategoryId(requestDto.getCategoryId() != null ? requestDto.getCategoryId().intValue() : 0);
        boardDAO.updateBoard(board);

        // 3. 기존 블록 삭제
        blockDAO.deleteBlocksByBoardId(boardId);

        // 4. 새로운 블록 저장
        List<Block> savedBlockEntities = new ArrayList<>();
        if (requestDto.getBlocks() != null && !requestDto.getBlocks().isEmpty()) {
            for (BoardUpdateRequestDto.BlockUpdateRequestDto blockDto : requestDto.getBlocks()) {
                Block block = Block.builder()
                        .boardId(boardId)
                        .orderIndex(blockDto.getOrderIndex())
                        .blockType(blockDto.getBlockType())
                        .contentText(blockDto.getContentText())
                        .imageUrl(blockDto.getImageUrl())
                        .imageCaption(blockDto.getImageCaption())
                        .build();
                blockDAO.saveBlock(block);
                savedBlockEntities.add(block);
            }
        }

        // 5. 업데이트된 게시글 정보 조회
        Board updatedBoard = boardDAO.findBoardById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("업데이트된 게시글을 찾을 수 없습니다."));

        User author = userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다."));

        return BoardDetailResponseDto.fromEntities(updatedBoard, author, savedBlockEntities);
    }

    @Override
    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        // 1. 게시글 존재 확인 및 작성자 확인
        Board board = boardDAO.findBoardById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. boardId: " + boardId));

        if (!board.getUserId().equals(userId)) {
            throw new IllegalStateException("게시글 삭제 권한이 없습니다.");
        }

        // 2. 관련 데이터 삭제 (블록, 댓글 등)
        blockDAO.deleteBlocksByBoardId(boardId);
        commentDAO.deleteCommentsByBoardId(boardId);

        // 3. 게시글 삭제
        boardDAO.deleteBoard(boardId);


    }

    @Override
    @Transactional
    public BoardLikeResponseDto toggleLike(Long userId, Long boardId) {
        // 1. 게시글 존재 확인
        Board board = boardDAO.findBoardById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 2. 좋아요 상태 확인
        BoardLike existingLike = boardLikeDAO.findByUserIdAndBoardId(userId, boardId);
        
        if (existingLike != null) {
            // 이미 좋아요가 있는 경우 -> 좋아요 취소
            boardLikeDAO.deleteLike(userId, boardId);
            boardLikeDAO.updateLikeCount2(boardId);
            board.setLikesCount(board.getLikesCount() - 1);
        } else {
            // 좋아요가 없는 경우 -> 좋아요 추가
            BoardLike newLike = BoardLike.builder()
                    .userId(userId)
                    .boardId(boardId)
                    .build();
            boardLikeDAO.saveLike(newLike);
            boardLikeDAO.updateLikeCount(newLike);
            board.setLikesCount(board.getLikesCount() + 1);
        }
        
        boardDAO.updateBoard(board);
        
        return BoardLikeResponseDto.builder()
                .boardId(boardId)
                .userId(userId)
                .isLiked(existingLike == null)
                .likesCount(board.getLikesCount())
                .build();
    }

    @Override
    public BoardLikeResponseDto getLikeStatus(Long userId, Long boardId) {
        // 1. 게시글 존재 확인
        Board board = boardDAO.findBoardById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
                
        // 2. 좋아요 상태 확인
        boolean isLiked = boardLikeDAO.existsByUserIdAndBoardId(userId, boardId) > 0;
        
        return BoardLikeResponseDto.builder()
                .boardId(boardId)
                .userId(userId)
                .isLiked(isLiked)
                .likesCount(board.getLikesCount())
                .build();
    }

    @Override
    public Page<BoardResponseDto> getLikedBoardsByUser(Long userId, int page, int size) {
        int offset = page * size;
        
        // 1. 좋아요한 게시글 목록 조회
        List<BoardResponseDto> likedBoards = boardLikeDAO.findLikedBoardsByUserId(userId, offset, size);
        
        // 2. 전체 좋아요 수 조회
        long totalElements = boardLikeDAO.countLikedBoardsByUserId(userId);
        
        return new PageImpl<>(likedBoards, PageRequest.of(page, size), totalElements);
    }

    @Override
    public int getLikesCount(Long boardId) {
        log.info("게시글 좋아요 수 조회 - boardId: {}", boardId);
        return boardLikeDAO.countByBoardId(boardId);
    }

}
