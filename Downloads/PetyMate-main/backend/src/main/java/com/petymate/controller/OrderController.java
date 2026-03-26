package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/cart/create-order")
    public ResponseEntity<ApiResponse<OrderDto.CartOrderResponse>> createCartOrder(
            Authentication auth, @Valid @RequestBody OrderDto.CreateCartOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(orderService.createCartOrder(auth.getName(), request)));
    }

    @PostMapping("/cart/verify")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponse>> verifyCartOrder(
            Authentication auth, @Valid @RequestBody OrderDto.VerifyPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Order confirmed!", orderService.verifyCartOrder(auth.getName(), request)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDto.OrderResponse>>> getMyOrders(
            Authentication auth, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getMyOrders(auth.getName(), PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponse>> getOrder(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(id, auth.getName())));
    }
}
