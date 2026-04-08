package com.codecart.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartOrderSubmitRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
