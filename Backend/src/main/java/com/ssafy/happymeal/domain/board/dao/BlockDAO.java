package com.ssafy.happymeal.domain.board.dao;

import com.ssafy.happymeal.domain.board.entity.Block;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BlockDAO {

    // DB 스키마의 create_at, update_at 컬럼명 사용
    @Insert("INSERT INTO Block (board_id, order_index, block_type, content_text, image_url, image_caption, create_at, update_at) " +
            "VALUES (#{boardId}, #{orderIndex}, #{blockType}, #{contentText}, #{imageUrl}, #{imageCaption}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "blockId", keyColumn = "block_id")
    int saveBlock(Block block);

    @Select("SELECT block_id, board_id, order_index, block_type, content_text, image_url, image_caption, create_at, update_at " +
            "FROM Block WHERE board_id = #{boardId} ORDER BY order_index ASC")
    List<Block> findBlocksByBoardId(Long boardId);
}
