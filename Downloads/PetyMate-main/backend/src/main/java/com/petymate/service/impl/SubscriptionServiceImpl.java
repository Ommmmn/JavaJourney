package com.petymate.service.impl;

import com.petymate.config.RazorpayConfig;
import com.petymate.dto.SubscriptionDto;
import com.petymate.entity.*;
import com.petymate.exception.*;
import com.petymate.repository.*;
import com.petymate.service.SubscriptionService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    @Override
    public List<SubscriptionDto.PlanDetail> getPlans() {
        return List.of(
            SubscriptionDto.PlanDetail.builder()
                .name("FREE").price(BigDecimal.ZERO).browsing("unlimited").requests("2/day")
                .unlocks("0").priorityListing(false).badge("").earlyAccess(false)
                .features(List.of("Browse all pet listings", "2 match requests/day", "Basic pet profile")).build(),
            SubscriptionDto.PlanDetail.builder()
                .name("BASIC").price(BigDecimal.valueOf(199)).browsing("unlimited").requests("10/day")
                .unlocks("5/month").priorityListing(false).badge("BASIC").earlyAccess(false)
                .features(List.of("Everything in Free", "10 match requests/day", "5 contact unlocks/month",
                        "View pet seller contacts", "Email match alerts")).build(),
            SubscriptionDto.PlanDetail.builder()
                .name("PREMIUM").price(BigDecimal.valueOf(499)).browsing("unlimited").requests("unlimited")
                .unlocks("unlimited").priorityListing(true).badge("PREMIUM").earlyAccess(true)
                .features(List.of("Everything in Basic", "Unlimited requests + unlocks", "Priority listing badge",
                        "Early access to new pets", "Trainer & vet discounts (10%)", "Dedicated support")).build()
        );
    }

    @Override
    @Transactional
    public SubscriptionDto.OrderResponse createOrder(String email, SubscriptionDto.CreateOrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        int amountPaise = "PREMIUM".equals(request.getPlan()) ? 49900 : 19900;

        try {
            JSONObject options = new JSONObject();
            options.put("amount", amountPaise);
            options.put("currency", "INR");
            options.put("receipt", "sub_" + UUID.randomUUID());
            Order order = razorpayClient.orders.create(options);

            Subscription sub = Subscription.builder()
                    .user(user)
                    .plan(Subscription.SubscriptionPlan.valueOf(request.getPlan()))
                    .razorpayOrderId(order.get("id"))
                    .amount(BigDecimal.valueOf(amountPaise / 100.0))
                    .status(Subscription.SubscriptionStatus.PENDING)
                    .build();
            subscriptionRepository.save(sub);

            return SubscriptionDto.OrderResponse.builder()
                    .orderId(order.get("id")).amount(amountPaise).currency("INR").keyId(razorpayConfig.getKeyId()).build();
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create subscription order", e);
        }
    }

    @Override
    @Transactional
    public SubscriptionDto.SubscriptionResponse verify(String email, SubscriptionDto.VerifyRequest request) {
        String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
        String expectedSig = new HmacUtils("HmacSHA256", razorpayConfig.getKeySecret()).hmacHex(payload);
        if (!expectedSig.equals(request.getRazorpaySignature())) {
            throw new PaymentVerificationException("Invalid payment signature");
        }

        Subscription sub = subscriptionRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription order not found"));

        sub.setRazorpayPaymentId(request.getRazorpayPaymentId());
        sub.setRazorpaySignature(request.getRazorpaySignature());
        sub.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        sub.setStartedAt(LocalDateTime.now());
        sub.setExpiresAt(LocalDateTime.now().plusDays(30));
        subscriptionRepository.save(sub);

        User user = sub.getUser();
        user.setSubscriptionTier(User.SubscriptionTier.valueOf(request.getPlan()));
        user.setSubscriptionExpiry(sub.getExpiresAt());
        userRepository.save(user);

        log.info("Subscription activated for user {}: {}", email, request.getPlan());

        return SubscriptionDto.SubscriptionResponse.builder()
                .id(sub.getId()).plan(sub.getPlan().name()).status(sub.getStatus().name())
                .tier(user.getSubscriptionTier().name()).amount(sub.getAmount())
                .startedAt(sub.getStartedAt()).expiresAt(sub.getExpiresAt()).build();
    }

    @Override
    public SubscriptionDto.SubscriptionResponse mySubscription(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSubscriptionExpiry() != null && user.getSubscriptionExpiry().isBefore(LocalDateTime.now())) {
            user.setSubscriptionTier(User.SubscriptionTier.FREE);
            user.setSubscriptionExpiry(null);
            userRepository.save(user);
        }

        return SubscriptionDto.SubscriptionResponse.builder()
                .tier(user.getSubscriptionTier().name())
                .expiresAt(user.getSubscriptionExpiry())
                .build();
    }
}
