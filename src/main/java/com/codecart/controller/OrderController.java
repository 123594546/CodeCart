package com.codecart.controller;

import com.codecart.common.context.UserContextHolder;
import com.codecart.common.result.ApiResult;
import com.codecart.dto.order.DirectOrderSubmitRequest;
import com.codecart.service.OrderService;
import com.codecart.vo.order.OrderDetailVO;
import com.codecart.vo.order.OrderSubmitResponse;
import com.codecart.vo.order.OrderSummaryVO;
import com.codecart.vo.order.PurchasedCodeVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/direct")
    public ApiResult<OrderSubmitResponse> createDirectOrder(
            @Valid @RequestBody DirectOrderSubmitRequest request) {
        return ApiResult.success(orderService.createDirectOrder(UserContextHolder.getRequiredUserId(), request));
    }

    @PostMapping("/cart")
    public ApiResult<OrderSubmitResponse> createCartOrder() {
        return ApiResult.success(orderService.createCartOrder(UserContextHolder.getRequiredUserId()));
    }

    @PostMapping("/{orderNo}/pay")
    public ApiResult<OrderDetailVO> payOrder(@PathVariable("orderNo") String orderNo) {
        return ApiResult.success(orderService.payOrder(UserContextHolder.getRequiredUserId(), orderNo));
    }

    @GetMapping
    public ApiResult<List<OrderSummaryVO>> listUserOrders() {
        return ApiResult.success(orderService.listUserOrders(UserContextHolder.getRequiredUserId()));
    }

    @GetMapping("/codes")
    public ApiResult<List<PurchasedCodeVO>> listPurchasedCodes() {
        return ApiResult.success(orderService.listPurchasedCodes(UserContextHolder.getRequiredUserId()));
    }

    @GetMapping("/{orderNo}")
    public ApiResult<OrderDetailVO> getOrderDetail(@PathVariable("orderNo") String orderNo) {
        return ApiResult.success(orderService.getOrderDetail(UserContextHolder.getRequiredUserId(), orderNo));
    }
}
