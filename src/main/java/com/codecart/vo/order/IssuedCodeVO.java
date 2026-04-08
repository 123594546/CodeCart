package com.codecart.vo.order;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class IssuedCodeVO {

    private Long redeemCodeId;

    private String codeValue;

    private String issueStatus;

    private LocalDateTime issueTime;
}
