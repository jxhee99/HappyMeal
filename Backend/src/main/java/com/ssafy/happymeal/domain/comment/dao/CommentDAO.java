package com.ssafy.happymeal.domain.comment.dao;

import com.ssafy.happymeal.domain.board.dto.CommentResponseDto;
import com.ssafy.happymeal.domain.comment.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentDAO {

    // 댓글 생성
    @Insert("Insert into Comment(board_id, user_id, content, parent_comment_id) " +
            "Values (#{boardId}, #{userId}, #{content}, #{parentCommentId})")
    @Options(useGeneratedKeys = true, keyProperty = "commentId", keyColumn = "comment_id")
    int saveComment(Comment comment);

    // 댓글 ID와 부모 댓글 ID 일치 확인
    @Select("select * " +
            "from Comment " +
            "where comment_id=#{commentId} and board_id=#{boardId}")
    Optional<Comment> findCommentById(Long commentId, Long boardId);


    // 특정 게시글의 모든 최상위 댓글 목록 조회
    @Select("""
            <script>
            Select c.comment_id, c.board_id, c.user_id, c.parent_comment_id, u.nickname, c.content, c.create_at, c.update_at
            From Comment c
            Inner Join User u On c.user_id = u.user_id
            Where c.board_id=#{boardId} And c.parent_comment_id is NULL
            Order by c.create_at
            </script>
            """)
    List<CommentResponseDto> findTopLevelCommentsByBoardId(Long boardId);

    // 여러 부모 댓글 ID에 해당하는 모든 직계 자식 댓글 조회
    @Select("""
            <script>
            Select c.comment_id, c.board_id, c.user_id, c.parent_comment_id, u.nickname, c.content, c.create_at, c.update_at
            From Comment c
            Inner Join User u On c.user_id = u.user_id
            Where c.parent_comment_id In
            <foreach item="parentId" collection="parentCommentIds" open="(" separator="," close=")">
                #{parentId}
            </foreach>
            Order by c.create_at
            </script>
            """)
    List<CommentResponseDto> findChildRepliesByParentId(@Param("parentCommentIds") List<Long> parentCommentIds);

    @Delete("DELETE FROM Comment WHERE board_id = #{boardId}")
    int deleteCommentsByBoardId(@Param("boardId") Long boardId);
}
