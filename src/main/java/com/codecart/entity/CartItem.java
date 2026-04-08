package com.codecart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.codecart.common.entity.BaseEntity;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cart_item")
public class CartItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private Integer checkedFlag;
}
