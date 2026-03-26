package com.petymate.service;

import com.petymate.dto.AdminDto;
import com.petymate.dto.PagedResponse;
import com.petymate.dto.AuthDto;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    AdminDto.DashboardResponse getDashboard();

    PagedResponse<AuthDto.UserDto> getUsers(
            String role,
            String tier,
            Boolean banned,
            Pageable pageable
    );

    void banUser(Long userId);

    void unbanUser(Long userId);

    void updateTier(Long userId, AdminDto.UpdateTierRequest request);

    void verifyVet(Long vetId);

    void verifyTrainer(Long trainerId);

    void updateOrderStatus(
            Long orderId,
            AdminDto.UpdateOrderStatusRequest request
    );

    PagedResponse<AdminDto.ReviewResponse> getReviews(
            String targetType,
            Long targetId,
            Pageable pageable
    );

    void deleteReview(Long reviewId);
}

