package com.ssafy.happymeal.domain.board.dao;

import com.ssafy.happymeal.domain.board.dto.*;
import com.ssafy.happymeal.domain.board.entity.Board;
import com.ssafy.happymeal.domain.comment.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardDAO {

    // 조건에 맞는 게시글 목록(페이징 처리된) 조회
    @Select("""
            <script>
            SELECT
                b.board_id as boardId,
                b.user_id as userId,
                u.nickname as nickName,
                b.category_id as categoryId,
                b.title as title,
                b.create_at as createAt,
                b.update_at as updateAt,
                b.views as views,
                b.likes_count as likes_count,
                b.comments_count as commentsCount
            FROM
                Board b
            INNER JOIN
                User u ON b.user_id = u.user_id
            <where>
                <if test="categoryId != null">
                    AND b.category_id = #{categoryId}
                </if>
            </where>
            <choose>
                <when test="sortBy == 'popular'">
                    ORDER BY b.likes_count DESC, b.views DESC, b.comments_count DESC, b.create_at DESC
                </when>
                <otherwise>
                    ORDER BY b.create_at DESC
                </otherwise>
            </choose>
            LIMIT #{size} OFFSET #{offset}
            </script>
            """)
    List<BoardResponseDto> findBoardByCategory(BoardCategoryCriteria criteria);

    // 전체 게시글 수 조회
    @Select("""
            <script>
            SELECT
                count(*)
            FROM
                Board b
            <where>
                <if test="categoryId != null">
                    AND b.category_id = #{categoryId}
                </if>
            </where>
            </script>
            """)
    long countBoardsByCategory(BoardCategoryCriteria criteria);

    // 게시글 전체 조회
    @Select("Select " +
            "b.board_id as boardId, " +
            "b.user_id as userId, " +
            "u.nickname as nickName," +
            "b.category_id as categoryId," +
            "b.title as title, " +
            "b.create_at as createAt," +
            "b.update_at as updateAt," +
            "b.views as views," +
            "b.likes_count as likes_count," +
            "b.comments_count as commentsCount " +
            "From Board b " +
            "Inner Join " +
            "User u On b.user_id = u.user_id")
    List<BoardResponseDto> findAll();

    // 게시글 검색 (제목으로 검색)
    @Select("""
            <script>
            SELECT
                b.board_id as boardId,
                b.user_id as userId,
                u.nickname as nickName,
                b.category_id as categoryId,
                b.title as title,
                b.create_at as createAt,
                b.update_at as updateAt,
                b.views as views,
                b.likes_count as likes_count,
                b.comments_count as commentsCount
            FROM
                Board b
            INNER JOIN
                User u ON b.user_id = u.user_id
            <where>
                <if test="title != null">
                    AND b.title Like Concat('%', #{title}, '%')
                </if>
            </where>
            <choose>
                <when test="sortBy == 'popular'">
                    ORDER BY b.likes_count DESC, b.views DESC, b.comments_count DESC, b.create_at DESC
                </when>
                <otherwise>
                    ORDER BY b.create_at DESC
                </otherwise>
            </choose>
            LIMIT #{size} OFFSET #{offset}
            </script>
            """)
    List<BoardResponseDto> searchBoardsByTitle(BoardTitleSearchCriteria criteria);

    // 제목과 일치하는 게시글 수 조회
    @Select("""
            <script>
            SELECT
                count(*)
            FROM
                Board b
            <where>
                <if test="title != null">
                    AND b.title Like Concat('%', #{title}, '%')
                </if>
            </where>
            </script>
            """)
    long countBoardsByTitle(BoardTitleSearchCriteria criteria);


    // 게시글 검색 (닉네임으로 검색)
    @Select("""
            <script>
            SELECT
                b.board_id as boardId,
                b.user_id as userId,
                u.nickname as nickName,
                b.category_id as categoryId,
                b.title as title,
                b.create_at as createAt,
                b.update_at as updateAt,
                b.views as views,
                b.likes_count as likes_count,
                b.comments_count as commentsCount
            FROM
                Board b
            INNER JOIN
                User u ON b.user_id = u.user_id
            <where>
                <if test="nickname != null">
                    AND u.nickname = #{nickname}
                </if>
            </where>
            <choose>
                <when test="sortBy == 'popular'">
                    ORDER BY b.likes_count DESC, b.views DESC, b.comments_count DESC, b.create_at DESC
                </when>
                <otherwise>
                    ORDER BY b.create_at DESC
                </otherwise>
            </choose>
            LIMIT #{size} OFFSET #{offset}
            </script>
            """)
    List<BoardResponseDto> searchBoardsByAuthor(BoardAuthorSearchCriteria criteria);

    // 작성자 닉네임과 일치하는 게시글 수 조회
    @Select("""
            <script>
            SELECT
                count(*)
            FROM
                Board b
            INNER JOIN
                User u ON b.user_id = u.user_id
            <where>
                <if test="nickname != nickname">
                    AND u.nickname = #{nickname}
                </if>
            </where>
            </script>
            """)
    long countBoardsByAuthor(BoardAuthorSearchCriteria criteria);

    String BASE_BOARD_COLUMNS = "board_id, user_id, category_id, title, create_at, update_at, views, likes_count, comments_count";

    // DB 스키마의 create_at, update_at 컬럼명 사용
    @Insert("INSERT INTO Board (user_id, category_id, title, create_at, update_at, views, likes_count, comments_count) " +
            "VALUES (#{userId}, #{categoryId}, #{title}, NOW(), NOW(), 0, 0, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "boardId", keyColumn = "board_id")
    int saveBoard(Board board);

    @Select("SELECT " + BASE_BOARD_COLUMNS + " FROM Board WHERE board_id = #{boardId}")
    Optional<Board> findBoardById(Long boardId);

    @Update("UPDATE Board SET views = views + 1 WHERE board_id = #{boardId}")
    int incrementViewCount(@Param("boardId") Long boardId);

    // 게시글 수정
    @Update("UPDATE Board SET title = #{title}, category_id = #{categoryId}, update_at = NOW() WHERE board_id = #{boardId}")
    int updateBoard(Board board);

    // 게시글 삭제
    @Delete("DELETE FROM Board WHERE board_id = #{boardId}")
    int deleteBoard(Long boardId);

}
