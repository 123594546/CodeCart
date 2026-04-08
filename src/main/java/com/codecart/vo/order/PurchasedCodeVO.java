package com.codecart.vo.order;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PurchasedCodeVO {

    private String orderNo;

    private Long productId;

    private String productName;

    private Long redeemCodeId;

    private String codeValue;

    private LocalDateTime issueTime;
}
