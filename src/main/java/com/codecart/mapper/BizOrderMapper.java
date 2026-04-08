package com.codecart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecart.entity.BizOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BizOrderMapper extends BaseMapper<BizOrder> {

    @Select("""
            SELECT *
            FROM biz_order
            WHERE order_no = #{orderNo}
              AND deleted = 0
            LIMIT 1
            FOR UPDATE
            """)
    BizOrder selectByOrderNoForUpdate(@Param("orderNo") String orderNo);
}
