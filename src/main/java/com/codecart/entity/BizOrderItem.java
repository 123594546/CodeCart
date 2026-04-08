package com.codecart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.codecart.common.entity.BaseEntity;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_order_item")
public class BizOrderItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;

    private String orderNo;

    private Long productId;

    private String productNameSnapshot;

    private String productCoverSnapshot;

    private BigDecimal priceSnapshot;

    private Integer quantity;

    private BigDecimal subtotalAmount;
}
