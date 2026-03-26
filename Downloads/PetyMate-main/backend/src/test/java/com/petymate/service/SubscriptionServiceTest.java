package com.petymate.service;

import com.petymate.config.RazorpayConfig;
import com.petymate.dto.SubscriptionDto;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.repository.*;
import com.petymate.service.impl.SubscriptionServiceImpl;
import com.razorpay.RazorpayClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionService Tests")
class SubscriptionServiceTest {

    @InjectMocks private SubscriptionServiceImpl subscriptionService;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private UserRepository userRepository;
    @Mock private RazorpayClient razorpayClient;
    @Mock private RazorpayConfig razorpayConfig;

    @Test @DisplayName("Get plans: returns 3 plans")
    void getPlans() {
        List<SubscriptionDto.PlanDetail> plans = subscriptionService.getPlans();
        assertEquals(3, plans.size());
        assertEquals("FREE", plans.get(0).getName());
        assertEquals("BASIC", plans.get(1).getName());
        assertEquals("PREMIUM", plans.get(2).getName());
        assertEquals(BigDecimal.valueOf(199), plans.get(1).getPrice());
        assertEquals(BigDecimal.valueOf(499), plans.get(2).getPrice());
    }

    @Test @DisplayName("My subscription: FREE user")
    void mySubscription_FreeUser() {
        User user = User.builder().email("john@test.com").subscriptionTier(User.SubscriptionTier.FREE).build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        SubscriptionDto.SubscriptionResponse resp = subscriptionService.mySubscription("john@test.com");
        assertEquals("FREE", resp.getTier());
    }

    @Test @DisplayName("My subscription: expired → reset to FREE")
    void mySubscription_Expired() {
        User user = User.builder().email("john@test.com").subscriptionTier(User.SubscriptionTier.PREMIUM)
                .subscriptionExpiry(LocalDateTime.now().minusDays(1)).build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        SubscriptionDto.SubscriptionResponse resp = subscriptionService.mySubscription("john@test.com");
        assertEquals("FREE", resp.getTier());
    }

    @Test @DisplayName("My subscription: user not found")
    void mySubscription_NotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.mySubscription("unknown@test.com"));
    }
}
