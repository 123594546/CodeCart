package com.codecart.vo.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderSubmitResponse {

    private Long orderId;

    private String orderNo;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private String orderStatus;

    private String payStatus;

    private String sourceType;

    private LocalDateTime createdAt;
}
