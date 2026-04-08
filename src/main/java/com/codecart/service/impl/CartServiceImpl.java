package com.codecart.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.codecart.common.constants.BusinessConstants;
import com.codecart.common.exception.BizException;
import com.codecart.dto.cart.CartAddRequest;
import com.codecart.dto.cart.CartUpdateRequest;
import com.codecart.entity.CartItem;
import com.codecart.entity.Product;
import com.codecart.mapper.CartItemMapper;
import com.codecart.mapper.ProductMapper;
import com.codecart.service.CartService;
import com.codecart.vo.cart.CartItemVO;
import com.codecart.vo.cart.CartSummaryVO;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    @Override
    public CartSummaryVO getCartSummary(Long userId) {
        return buildCartSummary(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartSummaryVO addToCart(Long userId, CartAddRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getDeleted() == 1) {
            throw new BizException("商品不存在");
        }
        if (!BusinessConstants.ProductStatus.ON_SALE.equals(product.getStatus())) {
            throw new BizException("商品已下架，无法加入购物车");
        }
        if (product.getAvailableStock() < request.getQuantity()) {
            throw new BizException("商品库存不足");
        }

        CartItem existing = cartItemMapper.selectOne(Wrappers.<CartItem>lambdaQuery()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, request.getProductId())
                .last("LIMIT 1"));
        if (existing == null) {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setCheckedFlag(1);
            cartItem.setRemark("前端加入购物车");
            cartItemMapper.insert(cartItem);
        } else {
            int newQuantity = existing.getQuantity() + request.getQuantity();
            if (product.getAvailableStock() < newQuantity) {
                throw new BizException("加入后超过可用库存");
            }
            cartItemMapper.update(null, Wrappers.<CartItem>lambdaUpdate()
                    .eq(CartItem::getId, existing.getId())
                    .eq(CartItem::getUserId, userId)
                    .set(CartItem::getQuantity, newQuantity)
                    .set(CartItem::getCheckedFlag, 1));
        }
        return buildCartSummary(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartSummaryVO updateCartItem(Long userId, CartUpdateRequest request) {
        CartItem cartItem = cartItemMapper.selectOne(Wrappers.<CartItem>lambdaQuery()
                .eq(CartItem::getId, request.getCartItemId())
                .eq(CartItem::getUserId, userId)
                .last("LIMIT 1"));
        if (cartItem == null) {
            throw new BizException("购物车项不存在");
        }

        Product product = productMapper.selectById(cartItem.getProductId());
        if (product == null || product.getDeleted() == 1) {
            throw new BizException("商品不存在");
        }
        if (request.getQuantity() != null) {
            if (product.getAvailableStock() < request.getQuantity()) {
                throw new BizException("商品库存不足");
            }
        }

        cartItemMapper.update(null, Wrappers.<CartItem>lambdaUpdate()
                .eq(CartItem::getId, cartItem.getId())
                .eq(CartItem::getUserId, userId)
                .set(request.getQuantity() != null, CartItem::getQuantity, request.getQuantity())
                .set(request.getCheckedFlag() != null, CartItem::getCheckedFlag, request.getCheckedFlag()));
        return buildCartSummary(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCartItem(Long userId, Long cartItemId) {
        int deletedRows = cartItemMapper.delete(Wrappers.<CartItem>lambdaQuery()
                .eq(CartItem::getId, cartItemId)
                .eq(CartItem::getUserId, userId));
        if (deletedRows != 1) {
            throw new BizException("购物车项不存在或已删除");
        }
    }

    private CartSummaryVO buildCartSummary(Long userId) {
        List<CartItem> cartItems = cartItemMapper.selectList(Wrappers.<CartItem>lambdaQuery()
                .eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getUpdatedAt, CartItem::getId));
        if (CollectionUtils.isEmpty(cartItems)) {
            CartSummaryVO emptySummary = new CartSummaryVO();
            emptySummary.setItems(Collections.emptyList());
            emptySummary.setTotalQuantity(0);
            emptySummary.setCheckedQuantity(0);
            emptySummary.setCheckedAmount(BigDecimal.ZERO);
            return emptySummary;
        }

        Map<Long, Product> productMap = productMapper.selectBatchIds(cartItems.stream()
                        .map(CartItem::getProductId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<CartItemVO> items = cartItems.stream().map(cartItem -> {
            Product product = productMap.get(cartItem.getProductId());
            CartItemVO itemVO = new CartItemVO();
            itemVO.setCartItemId(cartItem.getId());
            itemVO.setProductId(cartItem.getProductId());
            itemVO.setQuantity(cartItem.getQuantity());
            itemVO.setCheckedFlag(cartItem.getCheckedFlag());
            if (product != null) {
                itemVO.setProductName(product.getProductName());
                itemVO.setProductCover(product.getProductCover());
                itemVO.setPrice(product.getPrice());
                itemVO.setAvailableStock(product.getAvailableStock());
                itemVO.setProductStatus(product.getStatus());
                itemVO.setSubtotalAmount(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            } else {
                itemVO.setProductName("商品已失效");
                itemVO.setPrice(BigDecimal.ZERO);
                itemVO.setAvailableStock(0);
                itemVO.setProductStatus("INVALID");
                itemVO.setSubtotalAmount(BigDecimal.ZERO);
            }
            return itemVO;
        }).toList();

        CartSummaryVO summary = new CartSummaryVO();
        summary.setItems(items);
        summary.setTotalQuantity(items.stream().mapToInt(CartItemVO::getQuantity).sum());
        summary.setCheckedQuantity(items.stream()
                .filter(item -> item.getCheckedFlag() != null && item.getCheckedFlag() == 1)
                .mapToInt(CartItemVO::getQuantity)
                .sum());
        summary.setCheckedAmount(items.stream()
                .filter(item -> item.getCheckedFlag() != null && item.getCheckedFlag() == 1)
                .map(CartItemVO::getSubtotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return summary;
    }
}
