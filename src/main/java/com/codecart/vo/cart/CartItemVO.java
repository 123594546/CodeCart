package com.codecart.vo.cart;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CartItemVO {

    private Long cartItemId;

    private Long productId;

    private String productName;

    private String productCover;

    private BigDecimal price;

    private Integer quantity;

    private Integer checkedFlag;

    private Integer availableStock;

    private String productStatus;

    private BigDecimal subtotalAmount;
}
