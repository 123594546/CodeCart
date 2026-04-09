package com.codecart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codecart.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    @Update("""
            UPDATE product_category
            SET category_name = #{categoryName},
                sort_no = #{sortNo},
                status = #{status},
                remark = #{remark},
                updated_at = NOW()
            WHERE id = #{categoryId}
              AND deleted = 0
              AND NOT EXISTS (
                  SELECT 1
                  FROM product
                  WHERE category_id = #{categoryId}
                    AND status = 'ON_SALE'
                    AND deleted = 0
              )
            """)
    int updateWhenDisabling(
            @Param("categoryId") Long categoryId,
            @Param("categoryName") String categoryName,
            @Param("sortNo") Integer sortNo,
            @Param("status") String status,
            @Param("remark") String remark);

    @Update("""
            UPDATE product_category
            SET status = #{status},
                updated_at = NOW()
            WHERE id = #{categoryId}
              AND deleted = 0
              AND NOT EXISTS (
                  SELECT 1
                  FROM product
                  WHERE category_id = #{categoryId}
                    AND status = 'ON_SALE'
                    AND deleted = 0
              )
            """)
    int updateStatusWhenDisabling(@Param("categoryId") Long categoryId, @Param("status") String status);
}
