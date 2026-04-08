package com.codecart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.codecart.common.entity.BaseEntity;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long categoryId;

    private String productName;

    private String productCover;

    private String productDesc;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer totalStock;

    private Integer availableStock;

    private Integer soldCount;

    private String status;

    private Integer sortNo;
}
