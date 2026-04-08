package com.codecart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.codecart.common.entity.BaseEntity;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pay_record")
public class PayRecord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private String paymentNo;

    private Long orderId;

    private String orderNo;

    private Long userId;

    private BigDecimal payAmount;

    private String payMethod;

    private String payStatus;

    private String thirdPartyNo;

    private LocalDateTime paidAt;
}
