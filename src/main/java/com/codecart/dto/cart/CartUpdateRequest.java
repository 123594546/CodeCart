package com.codecart.dto.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartUpdateRequest {

    @NotNull(message = "购物车项ID不能为空")
    private Long cartItemId;

    @Min(value = 1, message = "购买数量必须大于0")
    private Integer quantity;

    @Min(value = 0, message = "勾选状态不合法")
    @Max(value = 1, message = "勾选状态不合法")
    private Integer checkedFlag;
}
