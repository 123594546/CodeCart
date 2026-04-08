package com.codecart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecart.entity.BizOrderItem;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BizOrderItemMapper extends BaseMapper<BizOrderItem> {

    @Select("""
            SELECT *
            FROM biz_order_item
            WHERE order_id = #{orderId}
              AND deleted = 0
            ORDER BY id
            FOR UPDATE
            """)
    List<BizOrderItem> selectByOrderIdForUpdate(@Param("orderId") Long orderId);
}
