package com.codecart.service;

import com.codecart.dto.order.CartOrderSubmitRequest;
import com.codecart.dto.order.DirectOrderSubmitRequest;
import com.codecart.dto.order.OrderPayRequest;
import com.codecart.vo.order.OrderDetailVO;
import com.codecart.vo.order.OrderSubmitResponse;
import com.codecart.vo.order.OrderSummaryVO;
import com.codecart.vo.order.PurchasedCodeVO;
import java.util.List;

public interface OrderService {

    OrderSubmitResponse createDirectOrder(Long userId, DirectOrderSubmitRequest request);

    OrderSubmitResponse createCartOrder(Long userId);

    OrderDetailVO payOrder(Long userId, String orderNo);

    List<OrderSummaryVO> listUserOrders(Long userId);

    OrderDetailVO getOrderDetail(Long userId, String orderNo);

    List<PurchasedCodeVO> listPurchasedCodes(Long userId);
}
