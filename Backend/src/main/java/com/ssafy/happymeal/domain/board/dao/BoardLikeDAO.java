package com.ssafy.happymeal.domain.board.dao;

import com.ssafy.happymeal.domain.board.entity.BoardLike;
import org.apache.ibatis.annotations.*;
import com.ssafy.happymeal.domain.board.dto.BoardResponseDto;
import java.util.List;

@Mapper
public interface BoardLikeDAO {
    
    @Insert("INSERT INTO BoardLike (user_id, board_id, create_at) VALUES (#{userId}, #{boardId}, NOW())")
    int saveLike(BoardLike boardLike);
    
    @Delete("DELETE FROM BoardLike WHERE user_id = #{userId} AND board_id = #{boardId}")
    int deleteLike(@Param("userId") Long userId, @Param("boardId") Long boardId);
    
    @Select("SELECT * FROM BoardLike WHERE user_id = #{userId} AND board_id = #{boardId}")
    BoardLike findByUserIdAndBoardId(@Param("userId") Long userId, @Param("boardId") Long boardId);
    
    @Select("SELECT COUNT(*) FROM BoardLike WHERE user_id = #{userId} AND board_id = #{boardId}")
    int existsByUserIdAndBoardId(@Param("userId") Long userId, @Param("boardId") Long boardId);
    
    @Select("""
            SELECT b.* FROM Board b
            INNER JOIN BoardLike bl ON b.board_id = bl.board_id
            WHERE bl.user_id = #{userId}
            ORDER BY bl.create_at DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    List<BoardResponseDto> findLikedBoardsByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);
    
    @Select("SELECT COUNT(*) FROM BoardLike WHERE user_id = #{userId}")
    long countLikedBoardsByUserId(Long userId);

    /**
     * 특정 게시글의 좋아요 수를 조회합니다.
     *
     * @param boardId 게시글 ID
     * @return 좋아요 수
     */
    @Select("SELECT COUNT(*) FROM BoardLike WHERE board_id = #{boardId}")
    int countByBoardId(Long boardId);
} 