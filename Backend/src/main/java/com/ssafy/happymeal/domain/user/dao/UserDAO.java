package com.ssafy.happymeal.domain.user.dao;

import com.ssafy.happymeal.domain.user.dto.MyBoardResponseDto;
import com.ssafy.happymeal.domain.user.dto.MyCommentResponseDto;
import com.ssafy.happymeal.domain.user.dto.MyPageCriteria;
import com.ssafy.happymeal.domain.user.dto.UserDto;
import com.ssafy.happymeal.domain.user.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserDAO {

    // 로그인을 위한 User google 아이디를 가져오기
    @Select("select * " +
            "from User " +
            "where google_id=#{googleId}")
    Optional<User> findByGoogleId(String googleId);

    // User 조회
    @Select("select * " +
            "from User " +
            "where user_id=#{userId}"
    )
    Optional<User> findById(Long userId);

    // User 정보 저장
    @Insert("insert into User(google_id, email, nickname, role, profile_image_url, create_at)"
            + " values(#{googleId}, #{email}, #{nickname}, #{role}, #{profileImageUrl}, NOW())"
    )
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    int save(User user);

    // User 정보 수정
    @Update("update User"
            + " set nickname=#{nickname}, profile_image_url=#{profileImageUrl}"
            + " where user_id=#{userId}"
    )
    int update(User user);

    // 사용자 정보 조회
    @Select("select user_id, email, nickname, profile_image_url " +
            "from User " +
            "Where user_id=#{userId}")
    UserDto getMyProfile(Long userId);

    // 사용자가 작성한 게시글 조회
    @Select("""
            <script>
            SELECT
                b.board_id as boardId,
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
            WHERE b.user_id=#{userId}
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
    List<MyBoardResponseDto> getMyPosts(MyPageCriteria criteria);

    // 작성한 게시글 수
    @Select("select count(*)" +
            "from Board " +
            "Where user_id=#{userId}")
    long countMyPosts(MyPageCriteria criteria);

    // 사용자가 작성한 댓글 조회
    @Select("""
            <script>
            SELECT
                c.comment_id as commentId,
                c.board_id as boardId,
                c.content as content,
                c.create_at as createAt,
                c.update_at as updateAt
            FROM
                Comment c
            INNER JOIN
                User u ON u.user_id = c.user_id
            WHERE c.user_id=#{userId}
            ORDER BY c.update_at DESC, c.create_at DESC
            LIMIT #{size} OFFSET #{offset}
            </script>
            """)
    List<MyCommentResponseDto> getMyComments(MyPageCriteria criteria);

    // 작성한 댓글 수
    @Select("select count(*)" +
            "from Comment " +
            "Where user_id=#{userId}")
    long countMyComments(MyPageCriteria criteria);

    // 좋아요 한 게시글 조회
    @Select("""
            <script>
            SELECT
                b.board_id as boardId,
                b.category_id as categoryId,
                b.title as title,
                b.create_at as createAt,
                b.update_at as updateAt,
                b.views as views,
                b.likes_count as likes_count,
                b.comments_count as commentsCount
            FROM
                BoardLike bl
            INNER JOIN
                Board b ON bl.board_id = b.board_id
            WHERE bl.user_id=#{userId}
            <choose>
                <when test="sortBy == 'popular'">
                    ORDER BY b.likes_count DESC, b.views DESC, b.comments_count DESC, b.create_at DESC
                </when>
                <otherwise>
                    ORDER BY bl.create_at DESC
                </otherwise>
            </choose>
            LIMIT #{size} OFFSET #{offset}
            </script>
            """)
    List<MyBoardResponseDto> getMyLikes(MyPageCriteria criteria);


    // 좋아요 한 게시글 수
    @Select("select count(*)" +
            "from BoardLike " +
            "Where user_id=#{userId}")
    Long countMyLikes(MyPageCriteria criteria);

    // 사용자 정보 수정
    @Update("update User set ")
    UserDto updateProfile(Long userId);

    // 닉네임 중복 검사
    @Select("select * from User where nickname=#{newNickname}")
    Optional<User> findByNickname(String newNickname);
}
