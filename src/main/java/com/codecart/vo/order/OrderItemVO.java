package com.codecart.vo.order;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class OrderItemVO {

    private Long orderItemId;

    private Long productId;

    private String productName;

    private String productCover;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotalAmount;

    private List<IssuedCodeVO> issuedCodes;
}
