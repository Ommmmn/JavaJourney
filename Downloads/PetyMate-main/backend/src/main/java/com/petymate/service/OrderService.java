package com.petymate.service;

import com.petymate.dto.OrderDto;
import com.petymate.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto.CartOrderResponse createCartOrder(String email, OrderDto.CreateCartOrderRequest request);
    OrderDto.OrderResponse verifyCartOrder(String email, OrderDto.VerifyPaymentRequest request);
    PagedResponse<OrderDto.OrderResponse> getMyOrders(String email, Pageable pageable);
    OrderDto.OrderResponse getOrderById(Long id, String email);
}
