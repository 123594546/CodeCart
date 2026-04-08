package com.codecart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.codecart.common.entity.BaseEntity;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("redeem_code_batch")
public class RedeemCodeBatch extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private String batchNo;

    private Long productId;

    private Integer importTotal;

    private Integer successTotal;

    private Integer failTotal;

    private String batchStatus;

    private Long importedBy;

    private LocalDateTime importedAt;
}
