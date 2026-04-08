package com.codecart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.codecart.common.entity.BaseEntity;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("code_issue_record")
public class CodeIssueRecord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;

    private String orderNo;

    private Long orderItemId;

    private Long userId;

    private Long productId;

    private Long redeemCodeId;

    private String issueStatus;

    private LocalDateTime issueTime;

    private String errorMessage;
}
