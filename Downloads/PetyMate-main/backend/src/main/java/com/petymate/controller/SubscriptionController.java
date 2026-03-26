package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<SubscriptionDto.PlanDetail>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getPlans()));
    }

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<SubscriptionDto.OrderResponse>> createOrder(
            Authentication auth, @Valid @RequestBody SubscriptionDto.CreateOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.createOrder(auth.getName(), request)));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<SubscriptionDto.SubscriptionResponse>> verify(
            Authentication auth, @Valid @RequestBody SubscriptionDto.VerifyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Subscription activated!", subscriptionService.verify(auth.getName(), request)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<SubscriptionDto.SubscriptionResponse>> mySubscription(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.mySubscription(auth.getName())));
    }
}
