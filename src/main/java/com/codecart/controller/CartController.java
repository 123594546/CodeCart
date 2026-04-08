package com.codecart.controller;

import com.codecart.common.context.UserContextHolder;
import com.codecart.common.result.ApiResult;
import com.codecart.dto.cart.CartAddRequest;
import com.codecart.dto.cart.CartUpdateRequest;
import com.codecart.service.CartService;
import com.codecart.vo.cart.CartSummaryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResult<CartSummaryVO> getCartSummary() {
        return ApiResult.success(cartService.getCartSummary(UserContextHolder.getRequiredUserId()));
    }

    @PostMapping
    public ApiResult<CartSummaryVO> addToCart(@Valid @RequestBody CartAddRequest request) {
        return ApiResult.success(cartService.addToCart(UserContextHolder.getRequiredUserId(), request));
    }

    @PatchMapping
    public ApiResult<CartSummaryVO> updateCartItem(@Valid @RequestBody CartUpdateRequest request) {
        return ApiResult.success(cartService.updateCartItem(UserContextHolder.getRequiredUserId(), request));
    }

    @DeleteMapping("/{cartItemId}")
    public ApiResult<Void> deleteCartItem(@PathVariable("cartItemId") Long cartItemId) {
        cartService.deleteCartItem(UserContextHolder.getRequiredUserId(), cartItemId);
        return ApiResult.success(null);
    }
}
