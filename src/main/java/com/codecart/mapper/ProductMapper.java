package com.codecart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecart.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("""
            UPDATE product
            SET total_stock = total_stock + #{quantity},
                available_stock = available_stock + #{quantity},
                updated_at = NOW()
            WHERE id = #{productId}
              AND deleted = 0
            """)
    int increaseStockAfterImport(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("""
            UPDATE product
            SET available_stock = available_stock - #{quantity},
                sold_count = sold_count + #{quantity},
                updated_at = NOW()
            WHERE id = #{productId}
              AND deleted = 0
              AND available_stock >= #{quantity}
            """)
    int deductStockAfterIssue(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
