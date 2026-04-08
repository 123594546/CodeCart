package com.codecart.service;

import com.codecart.dto.cart.CartAddRequest;
import com.codecart.dto.cart.CartUpdateRequest;
import com.codecart.vo.cart.CartSummaryVO;

public interface CartService {

    CartSummaryVO getCartSummary(Long userId);

    CartSummaryVO addToCart(Long userId, CartAddRequest request);

    CartSummaryVO updateCartItem(Long userId, CartUpdateRequest request);

    void deleteCartItem(Long userId, Long cartItemId);
}
