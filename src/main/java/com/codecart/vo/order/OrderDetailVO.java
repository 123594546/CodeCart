package com.codecart.vo.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderDetailVO {

    private Long orderId;

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private String orderStatus;

    private String payStatus;

    private String sourceType;

    private LocalDateTime payTime;

    private LocalDateTime completeTime;

    private LocalDateTime cancelTime;

    private LocalDateTime createdAt;

    private List<OrderItemVO> items;
}
