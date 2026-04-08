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
@TableName("biz_order")
public class BizOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

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
}
