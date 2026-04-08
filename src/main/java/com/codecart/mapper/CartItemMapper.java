package com.codecart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecart.entity.CartItem;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    @Select("""
            SELECT *
            FROM cart_item
            WHERE user_id = #{userId}
              AND checked_flag = 1
              AND deleted = 0
            ORDER BY updated_at DESC, id DESC
            """)
    List<CartItem> selectCheckedByUserId(@Param("userId") Long userId);
}
