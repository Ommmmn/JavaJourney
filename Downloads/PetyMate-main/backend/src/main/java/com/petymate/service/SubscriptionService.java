package com.petymate.service;

import com.petymate.dto.SubscriptionDto;
import java.util.List;

public interface SubscriptionService {
    List<SubscriptionDto.PlanDetail> getPlans();
    SubscriptionDto.OrderResponse createOrder(String email, SubscriptionDto.CreateOrderRequest request);
    SubscriptionDto.SubscriptionResponse verify(String email, SubscriptionDto.VerifyRequest request);
    SubscriptionDto.SubscriptionResponse mySubscription(String email);
}
