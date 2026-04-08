package com.codecart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.codecart.common.entity.BaseEntity;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("redeem_code")
public class RedeemCode extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long productId;

    private Long batchId;

    private String codeValue;

    private String codeStatus;

    private Long bindOrderId;

    private String bindOrderNo;

    private Long bindUserId;

    private LocalDateTime lockedTime;

    private LocalDateTime issuedTime;
}
