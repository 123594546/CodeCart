package com.codecart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecart.entity.RedeemCode;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RedeemCodeMapper extends BaseMapper<RedeemCode> {

    @Select("""
            SELECT *
            FROM redeem_code
            WHERE product_id = #{productId}
              AND code_status = 'UNUSED'
              AND deleted = 0
            ORDER BY id
            LIMIT #{limit}
            FOR UPDATE
            """)
    List<RedeemCode> selectUnusedCodesForUpdate(@Param("productId") Long productId, @Param("limit") Integer limit);
}
