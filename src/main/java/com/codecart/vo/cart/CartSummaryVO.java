package com.codecart.vo.cart;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CartSummaryVO {

    private List<CartItemVO> items;

    private Integer totalQuantity;

    private Integer checkedQuantity;

    private BigDecimal checkedAmount;
}
