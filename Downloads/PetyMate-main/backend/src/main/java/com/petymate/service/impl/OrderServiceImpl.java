package com.petymate.service.impl;

import com.petymate.config.RazorpayConfig;
import com.petymate.dto.OrderDto;
import com.petymate.dto.PagedResponse;
import com.petymate.entity.*;
import com.petymate.exception.*;
import com.petymate.repository.*;
import com.petymate.service.OrderService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    @Override
    @Transactional
    public OrderDto.CartOrderResponse createCartOrder(String email, OrderDto.CreateCartOrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BigDecimal total = BigDecimal.ZERO;
        List<OrderDto.OrderItemDto> itemDtos = new ArrayList<>();

        for (OrderDto.CartItem item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            if (product.getStockQty() < item.getQty()) {
                throw new IllegalArgumentException("Insufficient stock for: " + product.getName());
            }
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQty()));
            total = total.add(lineTotal);
            itemDtos.add(OrderDto.OrderItemDto.builder()
                    .productId(product.getId()).productName(product.getName())
                    .productPhoto(product.getPhotoUrl()).qty(item.getQty()).unitPrice(product.getPrice()).build());
        }

        BigDecimal shipping = total.compareTo(BigDecimal.valueOf(499)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(49);
        total = total.add(shipping);

        try {
            JSONObject options = new JSONObject();
            options.put("amount", total.multiply(BigDecimal.valueOf(100)).intValue());
            options.put("currency", "INR");
            options.put("receipt", "order_" + UUID.randomUUID());
            com.razorpay.Order rzpOrder = razorpayClient.orders.create(options);

            Order order = Order.builder()
                    .user(user).razorpayOrderId(rzpOrder.get("id")).totalAmount(total)
                    .status(Order.OrderStatus.PENDING)
                    .shippingName(request.getShippingAddress().getName())
                    .shippingPhone(request.getShippingAddress().getPhone())
                    .shippingAddress(request.getShippingAddress().getAddress())
                    .shippingCity(request.getShippingAddress().getCity())
                    .shippingState(request.getShippingAddress().getState())
                    .shippingPincode(request.getShippingAddress().getPincode())
                    .build();
            orderRepository.save(order);

            for (OrderDto.CartItem item : request.getItems()) {
                Product product = productRepository.findById(item.getProductId()).orElseThrow();
                OrderItem oi = OrderItem.builder()
                        .order(order).product(product).qty(item.getQty()).unitPrice(product.getPrice()).build();
                orderItemRepository.save(oi);
            }

            return OrderDto.CartOrderResponse.builder()
                    .razorpayOrderId(rzpOrder.get("id"))
                    .amount(total.multiply(BigDecimal.valueOf(100)).intValue())
                    .currency("INR").keyId(razorpayConfig.getKeyId()).orderItems(itemDtos).build();
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create order", e);
        }
    }

    @Override
    @Transactional
    public OrderDto.OrderResponse verifyCartOrder(String email, OrderDto.VerifyPaymentRequest request) {
        String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
        String expected = new HmacUtils("HmacSHA256", razorpayConfig.getKeySecret()).hmacHex(payload);
        if (!expected.equals(request.getRazorpaySignature())) {
            throw new PaymentVerificationException("Invalid payment signature");
        }

        Order order = orderRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setRazorpayPaymentId(request.getRazorpayPaymentId());
        order.setRazorpaySignature(request.getRazorpaySignature());
        order.setStatus(Order.OrderStatus.PAID);

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStockQty(product.getStockQty() - item.getQty());
            productRepository.save(product);
        }
        orderRepository.save(order);
        log.info("Order verified and paid: {}", order.getId());
        return mapToResponse(order);
    }

    @Override
    public PagedResponse<OrderDto.OrderResponse> getMyOrders(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<Order> page = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return PagedResponse.<OrderDto.OrderResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override
    public OrderDto.OrderResponse getOrderById(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("Access denied to this order");
        }
        return mapToResponse(order);
    }

    private OrderDto.OrderResponse mapToResponse(Order order) {
        List<OrderDto.OrderItemDto> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(i -> OrderDto.OrderItemDto.builder()
                        .productId(i.getProduct().getId()).productName(i.getProduct().getName())
                        .productPhoto(i.getProduct().getPhotoUrl()).qty(i.getQty()).unitPrice(i.getUnitPrice()).build())
                .collect(Collectors.toList());
        return OrderDto.OrderResponse.builder()
                .id(order.getId()).totalAmount(order.getTotalAmount()).status(order.getStatus().name())
                .shippingName(order.getShippingName()).shippingPhone(order.getShippingPhone())
                .shippingAddress(order.getShippingAddress()).shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState()).shippingPincode(order.getShippingPincode())
                .trackingNumber(order.getTrackingNumber()).items(items).createdAt(order.getCreatedAt()).build();
    }
}
