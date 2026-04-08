package com.codecart.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.codecart.common.constants.BusinessConstants;
import com.codecart.common.exception.BizException;
import com.codecart.common.util.BusinessNoGenerator;
import com.codecart.dto.order.DirectOrderSubmitRequest;
import com.codecart.entity.BizOrder;
import com.codecart.entity.BizOrderItem;
import com.codecart.entity.CartItem;
import com.codecart.entity.CodeIssueRecord;
import com.codecart.entity.PayRecord;
import com.codecart.entity.Product;
import com.codecart.entity.RedeemCode;
import com.codecart.mapper.BizOrderItemMapper;
import com.codecart.mapper.BizOrderMapper;
import com.codecart.mapper.CartItemMapper;
import com.codecart.mapper.CodeIssueRecordMapper;
import com.codecart.mapper.PayRecordMapper;
import com.codecart.mapper.ProductMapper;
import com.codecart.mapper.RedeemCodeMapper;
import com.codecart.service.OrderService;
import com.codecart.vo.order.IssuedCodeVO;
import com.codecart.vo.order.OrderDetailVO;
import com.codecart.vo.order.OrderItemVO;
import com.codecart.vo.order.OrderSubmitResponse;
import com.codecart.vo.order.OrderSummaryVO;
import com.codecart.vo.order.PurchasedCodeVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BizOrderMapper bizOrderMapper;
    private final BizOrderItemMapper bizOrderItemMapper;
    private final CartItemMapper cartItemMapper;
    private final CodeIssueRecordMapper codeIssueRecordMapper;
    private final PayRecordMapper payRecordMapper;
    private final ProductMapper productMapper;
    private final RedeemCodeMapper redeemCodeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitResponse createDirectOrder(Long userId, DirectOrderSubmitRequest request) {
        Product product = requirePurchasableProduct(request.getProductId(), request.getQuantity());
        BizOrder order = createOrder(
                userId,
                List.of(new OrderLineSnapshot(product, request.getQuantity())),
                BusinessConstants.OrderSourceType.DIRECT,
                "立即购买订单");
        return toOrderSubmitResponse(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitResponse createCartOrder(Long userId) {
        List<CartItem> cartItems = cartItemMapper.selectCheckedByUserId(userId);
        if (CollectionUtils.isEmpty(cartItems)) {
            throw new BizException("购物车中没有可结算的商品");
        }

        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).distinct().toList();
        Map<Long, Product> productMap = listProductsByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<OrderLineSnapshot> lines = new ArrayList<>(cartItems.size());
        for (CartItem cartItem : cartItems) {
            Product product = productMap.get(cartItem.getProductId());
            if (product == null || product.getDeleted() == 1) {
                throw new BizException("购物车中存在无效商品，请刷新后重试");
            }
            validatePurchasableProduct(product, cartItem.getQuantity());
            lines.add(new OrderLineSnapshot(product, cartItem.getQuantity()));
        }

        BizOrder order = createOrder(
                userId,
                lines,
                BusinessConstants.OrderSourceType.CART,
                "购物车结算订单");
        cartItemMapper.deleteBatchIds(cartItems.stream().map(CartItem::getId).toList());
        return toOrderSubmitResponse(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO payOrder(Long userId, String orderNo) {
        BizOrder order = bizOrderMapper.selectByOrderNoForUpdate(orderNo);
        if (order == null || order.getDeleted() == 1) {
            throw new BizException("订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new BizException("无权支付该订单");
        }
        if (BusinessConstants.OrderPayStatus.PAID.equals(order.getPayStatus())
                && BusinessConstants.OrderStatus.COMPLETED.equals(order.getOrderStatus())) {
            return getOrderDetail(userId, orderNo);
        }
        if (!BusinessConstants.OrderPayStatus.UNPAID.equals(order.getPayStatus())
                || !BusinessConstants.OrderStatus.PENDING_PAYMENT.equals(order.getOrderStatus())) {
            throw new BizException("当前订单状态不允许支付");
        }

        List<BizOrderItem> orderItems = bizOrderItemMapper.selectByOrderIdForUpdate(order.getId());
        if (CollectionUtils.isEmpty(orderItems)) {
            throw new BizException("订单明细不存在，无法支付");
        }

        PayRecord payRecord = payRecordMapper.selectOne(Wrappers.<PayRecord>lambdaQuery()
                .eq(PayRecord::getOrderId, order.getId())
                .last("LIMIT 1"));
        if (payRecord == null) {
            payRecord = createWaitPayRecord(order);
        }
        if (BusinessConstants.PayRecordStatus.SUCCESS.equals(payRecord.getPayStatus())) {
            return getOrderDetail(userId, orderNo);
        }
        if (!BusinessConstants.PayRecordStatus.WAIT_PAY.equals(payRecord.getPayStatus())) {
            throw new BizException("当前支付记录状态不允许继续支付");
        }

        LocalDateTime now = LocalDateTime.now();
        String thirdPartyNo = BusinessNoGenerator.nextThirdPartyNo();

        for (BizOrderItem orderItem : orderItems) {
            List<RedeemCode> codes = redeemCodeMapper.selectUnusedCodesForUpdate(
                    orderItem.getProductId(), orderItem.getQuantity());
            if (codes.size() < orderItem.getQuantity()) {
                throw new BizException(orderItem.getProductNameSnapshot() + " 库存不足，支付失败");
            }

            List<Long> codeIds = codes.stream().map(RedeemCode::getId).toList();
            int lockedRows = redeemCodeMapper.update(null, Wrappers.<RedeemCode>lambdaUpdate()
                    .in(RedeemCode::getId, codeIds)
                    .eq(RedeemCode::getCodeStatus, BusinessConstants.CodeStatus.UNUSED)
                    .set(RedeemCode::getCodeStatus, BusinessConstants.CodeStatus.LOCKED)
                    .set(RedeemCode::getBindOrderId, order.getId())
                    .set(RedeemCode::getBindOrderNo, order.getOrderNo())
                    .set(RedeemCode::getBindUserId, order.getUserId())
                    .set(RedeemCode::getLockedTime, now));
            if (lockedRows != codeIds.size()) {
                throw new BizException("兑换码锁定失败，请重试");
            }

            int issuedRows = redeemCodeMapper.update(null, Wrappers.<RedeemCode>lambdaUpdate()
                    .in(RedeemCode::getId, codeIds)
                    .eq(RedeemCode::getCodeStatus, BusinessConstants.CodeStatus.LOCKED)
                    .eq(RedeemCode::getBindOrderId, order.getId())
                    .set(RedeemCode::getCodeStatus, BusinessConstants.CodeStatus.ISSUED)
                    .set(RedeemCode::getIssuedTime, now));
            if (issuedRows != codeIds.size()) {
                throw new BizException("兑换码发放失败，请重试");
            }

            for (RedeemCode code : codes) {
                CodeIssueRecord issueRecord = new CodeIssueRecord();
                issueRecord.setOrderId(order.getId());
                issueRecord.setOrderNo(order.getOrderNo());
                issueRecord.setOrderItemId(orderItem.getId());
                issueRecord.setUserId(order.getUserId());
                issueRecord.setProductId(orderItem.getProductId());
                issueRecord.setRedeemCodeId(code.getId());
                issueRecord.setIssueStatus(BusinessConstants.IssueStatus.SUCCESS);
                issueRecord.setIssueTime(now);
                issueRecord.setRemark("模拟支付后自动发码成功");
                codeIssueRecordMapper.insert(issueRecord);
            }

            int stockUpdated = productMapper.deductStockAfterIssue(orderItem.getProductId(), orderItem.getQuantity());
            if (stockUpdated != 1) {
                throw new BizException(orderItem.getProductNameSnapshot() + " 聚合库存扣减失败");
            }
        }

        payRecordMapper.update(null, Wrappers.<PayRecord>lambdaUpdate()
                .eq(PayRecord::getId, payRecord.getId())
                .eq(PayRecord::getPayStatus, BusinessConstants.PayRecordStatus.WAIT_PAY)
                .set(PayRecord::getPayStatus, BusinessConstants.PayRecordStatus.SUCCESS)
                .set(PayRecord::getPaidAt, now)
                .set(PayRecord::getThirdPartyNo, thirdPartyNo));

        int updatedOrderRows = bizOrderMapper.update(null, Wrappers.<BizOrder>lambdaUpdate()
                .eq(BizOrder::getId, order.getId())
                .eq(BizOrder::getPayStatus, BusinessConstants.OrderPayStatus.UNPAID)
                .eq(BizOrder::getOrderStatus, BusinessConstants.OrderStatus.PENDING_PAYMENT)
                .set(BizOrder::getPayStatus, BusinessConstants.OrderPayStatus.PAID)
                .set(BizOrder::getOrderStatus, BusinessConstants.OrderStatus.COMPLETED)
                .set(BizOrder::getPayTime, now)
                .set(BizOrder::getCompleteTime, now));
        if (updatedOrderRows != 1) {
            throw new BizException("订单支付状态更新失败");
        }

        return getOrderDetail(userId, orderNo);
    }

    @Override
    public List<OrderSummaryVO> listUserOrders(Long userId) {
        return bizOrderMapper.selectList(Wrappers.<BizOrder>lambdaQuery()
                        .eq(BizOrder::getUserId, userId)
                        .orderByDesc(BizOrder::getCreatedAt, BizOrder::getId))
                .stream()
                .map(this::toOrderSummary)
                .toList();
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, String orderNo) {
        BizOrder order = bizOrderMapper.selectOne(Wrappers.<BizOrder>lambdaQuery()
                .eq(BizOrder::getOrderNo, orderNo)
                .eq(BizOrder::getUserId, userId)
                .last("LIMIT 1"));
        if (order == null) {
            throw new BizException("订单不存在");
        }

        List<BizOrderItem> orderItems = bizOrderItemMapper.selectList(Wrappers.<BizOrderItem>lambdaQuery()
                .eq(BizOrderItem::getOrderId, order.getId())
                .orderByAsc(BizOrderItem::getId));
        return buildOrderDetail(order, orderItems);
    }

    @Override
    public List<PurchasedCodeVO> listPurchasedCodes(Long userId) {
        List<CodeIssueRecord> issueRecords = codeIssueRecordMapper.selectList(Wrappers.<CodeIssueRecord>lambdaQuery()
                .eq(CodeIssueRecord::getUserId, userId)
                .eq(CodeIssueRecord::getIssueStatus, BusinessConstants.IssueStatus.SUCCESS)
                .orderByDesc(CodeIssueRecord::getIssueTime, CodeIssueRecord::getId));
        if (CollectionUtils.isEmpty(issueRecords)) {
            return Collections.emptyList();
        }

        Map<Long, RedeemCode> codeMap = listRedeemCodesByIds(issueRecords.stream()
                        .map(CodeIssueRecord::getRedeemCodeId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(RedeemCode::getId, Function.identity()));

        Map<Long, BizOrderItem> orderItemMap = listOrderItemsByIds(issueRecords.stream()
                        .map(CodeIssueRecord::getOrderItemId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(BizOrderItem::getId, Function.identity()));

        return issueRecords.stream().map(issueRecord -> {
            PurchasedCodeVO purchasedCode = new PurchasedCodeVO();
            purchasedCode.setOrderNo(issueRecord.getOrderNo());
            purchasedCode.setProductId(issueRecord.getProductId());
            purchasedCode.setIssueTime(issueRecord.getIssueTime());

            BizOrderItem orderItem = orderItemMap.get(issueRecord.getOrderItemId());
            if (orderItem != null) {
                purchasedCode.setProductName(orderItem.getProductNameSnapshot());
            }

            RedeemCode redeemCode = codeMap.get(issueRecord.getRedeemCodeId());
            if (redeemCode != null) {
                purchasedCode.setRedeemCodeId(redeemCode.getId());
                purchasedCode.setCodeValue(redeemCode.getCodeValue());
            }
            return purchasedCode;
        }).toList();
    }

    private Product requirePurchasableProduct(Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new BizException("商品不存在");
        }
        validatePurchasableProduct(product, quantity);
        return product;
    }

    private void validatePurchasableProduct(Product product, Integer quantity) {
        if (!BusinessConstants.ProductStatus.ON_SALE.equals(product.getStatus())) {
            throw new BizException(product.getProductName() + " 已下架");
        }
        if (product.getAvailableStock() == null || product.getAvailableStock() < quantity) {
            throw new BizException(product.getProductName() + " 库存不足");
        }
    }

    private BizOrder createOrder(Long userId, List<OrderLineSnapshot> lines, String sourceType, String remark) {
        BigDecimal totalAmount = lines.stream()
                .map(line -> line.product().getPrice().multiply(BigDecimal.valueOf(line.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BizOrder order = new BizOrder();
        order.setOrderNo(BusinessNoGenerator.nextOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setOrderStatus(BusinessConstants.OrderStatus.PENDING_PAYMENT);
        order.setPayStatus(BusinessConstants.OrderPayStatus.UNPAID);
        order.setSourceType(sourceType);
        order.setRemark(remark);
        bizOrderMapper.insert(order);

        for (OrderLineSnapshot line : lines) {
            BizOrderItem orderItem = new BizOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(order.getOrderNo());
            orderItem.setProductId(line.product().getId());
            orderItem.setProductNameSnapshot(line.product().getProductName());
            orderItem.setProductCoverSnapshot(line.product().getProductCover());
            orderItem.setPriceSnapshot(line.product().getPrice());
            orderItem.setQuantity(line.quantity());
            orderItem.setSubtotalAmount(line.product().getPrice().multiply(BigDecimal.valueOf(line.quantity())));
            orderItem.setRemark("订单商品快照");
            bizOrderItemMapper.insert(orderItem);
        }

        createWaitPayRecord(order);
        return order;
    }

    private PayRecord createWaitPayRecord(BizOrder order) {
        PayRecord payRecord = new PayRecord();
        payRecord.setPaymentNo(BusinessNoGenerator.nextPaymentNo());
        payRecord.setOrderId(order.getId());
        payRecord.setOrderNo(order.getOrderNo());
        payRecord.setUserId(order.getUserId());
        payRecord.setPayAmount(order.getPayAmount());
        payRecord.setPayMethod(BusinessConstants.PayMethod.MOCK);
        payRecord.setPayStatus(BusinessConstants.PayRecordStatus.WAIT_PAY);
        payRecord.setRemark("订单创建时生成模拟支付记录");
        payRecordMapper.insert(payRecord);
        return payRecord;
    }

    private OrderSubmitResponse toOrderSubmitResponse(BizOrder order) {
        OrderSubmitResponse response = new OrderSubmitResponse();
        response.setOrderId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setTotalAmount(order.getTotalAmount());
        response.setPayAmount(order.getPayAmount());
        response.setOrderStatus(order.getOrderStatus());
        response.setPayStatus(order.getPayStatus());
        response.setSourceType(order.getSourceType());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }

    private OrderSummaryVO toOrderSummary(BizOrder order) {
        OrderSummaryVO summary = new OrderSummaryVO();
        summary.setOrderId(order.getId());
        summary.setOrderNo(order.getOrderNo());
        summary.setTotalAmount(order.getTotalAmount());
        summary.setPayAmount(order.getPayAmount());
        summary.setOrderStatus(order.getOrderStatus());
        summary.setPayStatus(order.getPayStatus());
        summary.setSourceType(order.getSourceType());
        summary.setPayTime(order.getPayTime());
        summary.setCompleteTime(order.getCompleteTime());
        summary.setCreatedAt(order.getCreatedAt());
        return summary;
    }

    private OrderDetailVO buildOrderDetail(BizOrder order, List<BizOrderItem> orderItems) {
        List<CodeIssueRecord> issueRecords = codeIssueRecordMapper.selectList(Wrappers.<CodeIssueRecord>lambdaQuery()
                .eq(CodeIssueRecord::getOrderId, order.getId())
                .eq(CodeIssueRecord::getIssueStatus, BusinessConstants.IssueStatus.SUCCESS)
                .orderByAsc(CodeIssueRecord::getIssueTime, CodeIssueRecord::getId));

        Map<Long, List<CodeIssueRecord>> issueRecordMapByOrderItemId = issueRecords.stream()
                .filter(record -> record.getOrderItemId() != null)
                .collect(Collectors.groupingBy(CodeIssueRecord::getOrderItemId, LinkedHashMap::new, Collectors.toList()));
        Map<Long, List<CodeIssueRecord>> fallbackIssueRecordMapByProductId = issueRecords.stream()
                .filter(record -> record.getOrderItemId() == null)
                .collect(Collectors.groupingBy(CodeIssueRecord::getProductId, LinkedHashMap::new, Collectors.toList()));

        Map<Long, RedeemCode> codeMap = listRedeemCodesByIds(issueRecords.stream()
                        .map(CodeIssueRecord::getRedeemCodeId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(RedeemCode::getId, Function.identity()));

        List<OrderItemVO> itemVos = orderItems.stream().map(orderItem -> {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setOrderItemId(orderItem.getId());
            itemVO.setProductId(orderItem.getProductId());
            itemVO.setProductName(orderItem.getProductNameSnapshot());
            itemVO.setProductCover(orderItem.getProductCoverSnapshot());
            itemVO.setPrice(orderItem.getPriceSnapshot());
            itemVO.setQuantity(orderItem.getQuantity());
            itemVO.setSubtotalAmount(orderItem.getSubtotalAmount());

            List<CodeIssueRecord> itemIssueRecords = new ArrayList<>(issueRecordMapByOrderItemId
                    .getOrDefault(orderItem.getId(), Collections.emptyList()));
            if (itemIssueRecords.isEmpty()) {
                itemIssueRecords.addAll(fallbackIssueRecordMapByProductId
                        .getOrDefault(orderItem.getProductId(), Collections.emptyList()));
            }
            itemVO.setIssuedCodes(itemIssueRecords.stream().map(issueRecord -> {
                IssuedCodeVO issuedCode = new IssuedCodeVO();
                issuedCode.setRedeemCodeId(issueRecord.getRedeemCodeId());
                issuedCode.setIssueStatus(issueRecord.getIssueStatus());
                issuedCode.setIssueTime(issueRecord.getIssueTime());
                RedeemCode redeemCode = codeMap.get(issueRecord.getRedeemCodeId());
                if (redeemCode != null) {
                    issuedCode.setCodeValue(redeemCode.getCodeValue());
                }
                return issuedCode;
            }).toList());
            return itemVO;
        }).toList();

        OrderDetailVO detail = new OrderDetailVO();
        detail.setOrderId(order.getId());
        detail.setOrderNo(order.getOrderNo());
        detail.setUserId(order.getUserId());
        detail.setTotalAmount(order.getTotalAmount());
        detail.setPayAmount(order.getPayAmount());
        detail.setOrderStatus(order.getOrderStatus());
        detail.setPayStatus(order.getPayStatus());
        detail.setSourceType(order.getSourceType());
        detail.setPayTime(order.getPayTime());
        detail.setCompleteTime(order.getCompleteTime());
        detail.setCancelTime(order.getCancelTime());
        detail.setCreatedAt(order.getCreatedAt());
        detail.setItems(itemVos);
        return detail;
    }

    private List<Product> listProductsByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return productMapper.selectBatchIds(ids);
    }

    private List<BizOrderItem> listOrderItemsByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return bizOrderItemMapper.selectBatchIds(ids);
    }

    private List<RedeemCode> listRedeemCodesByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return redeemCodeMapper.selectBatchIds(ids);
    }

    private record OrderLineSnapshot(Product product, Integer quantity) {
    }
}
