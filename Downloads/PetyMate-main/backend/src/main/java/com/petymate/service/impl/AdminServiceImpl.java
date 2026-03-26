package com.petymate.service.impl;

import com.petymate.dto.*;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.repository.*;
import com.petymate.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final MatchRequestRepository matchRequestRepository;
    private final OrderRepository orderRepository;
    private final VetRepository vetRepository;
    private final TrainerRepository trainerRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public AdminDto.DashboardResponse getDashboard() {
        return AdminDto.DashboardResponse.builder()
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countByIsBannedFalse())
                .todayRegistrations(userRepository.countTodayRegistrations())
                .totalPets(petRepository.count())
                .activePetListings(petRepository.countByStatus(Pet.PetStatus.ACTIVE))
                .totalMatches(matchRequestRepository.count())
                .successfulMatches(matchRequestRepository.countSuccessfulMatches())
                .totalOrders(orderRepository.count())
                .todayOrders(orderRepository.countTodayOrders())
                .monthlyRevenue(orderRepository.calculateMonthlyRevenue())
                .totalRevenue(orderRepository.calculateTotalRevenue())
                .pendingVetVerifications(vetRepository.countByIsVerifiedFalse())
                .pendingTrainerVerifications(trainerRepository.countByIsVerifiedFalse())
                .build();
    }

    @Override
    public PagedResponse<AuthDto.UserDto> getUsers(String role, String tier, Boolean banned, Pageable pageable) {
        User.Role roleEnum = role != null ? User.Role.valueOf(role) : null;
        User.SubscriptionTier tierEnum = tier != null ? User.SubscriptionTier.valueOf(tier) : null;
        Page<User> page = userRepository.findAllFiltered(roleEnum, tierEnum, banned, pageable);
        return PagedResponse.<AuthDto.UserDto>builder()
                .content(page.getContent().stream().map(this::mapUser).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override @Transactional
    public void banUser(Long userId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setIsBanned(true); userRepository.save(u);
    }

    @Override @Transactional
    public void unbanUser(Long userId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setIsBanned(false); userRepository.save(u);
    }

    @Override @Transactional
    public void updateTier(Long userId, AdminDto.UpdateTierRequest request) {
        User u = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setSubscriptionTier(User.SubscriptionTier.valueOf(request.getTier()));
        userRepository.save(u);
    }

    @Override @Transactional
    public void verifyVet(Long vetId) {
        Vet v = vetRepository.findById(vetId).orElseThrow(() -> new ResourceNotFoundException("Vet not found"));
        v.setIsVerified(true); vetRepository.save(v);
    }

    @Override @Transactional
    public void verifyTrainer(Long trainerId) {
        Trainer t = trainerRepository.findById(trainerId).orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        t.setIsVerified(true); trainerRepository.save(t);
    }

    @Override @Transactional
    public void updateOrderStatus(Long orderId, AdminDto.UpdateOrderStatusRequest request) {
        Order o = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        o.setStatus(Order.OrderStatus.valueOf(request.getStatus()));
        if (request.getTrackingNumber() != null) o.setTrackingNumber(request.getTrackingNumber());
        orderRepository.save(o);
    }

    @Override
    public PagedResponse<AdminDto.ReviewResponse> getReviews(String targetType, Long targetId, Pageable pageable) {
        Review.TargetType type = Review.TargetType.valueOf(targetType);
        Page<Review> page = reviewRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(type, targetId, pageable);
        return PagedResponse.<AdminDto.ReviewResponse>builder()
                .content(page.getContent().stream().map(this::mapReview).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private AuthDto.UserDto mapUser(User u) {
        return AuthDto.UserDto.builder()
                .id(u.getId()).name(u.getName()).email(u.getEmail()).phone(u.getPhone())
                .city(u.getCity()).state(u.getState()).pincode(u.getPincode())
                .profilePhotoUrl(u.getProfilePhotoUrl()).role(u.getRole().name())
                .subscriptionTier(u.getSubscriptionTier().name()).subscriptionExpiry(u.getSubscriptionExpiry())
                .isVerified(u.getIsVerified()).createdAt(u.getCreatedAt()).build();
    }

    private AdminDto.ReviewResponse mapReview(Review r) {
        return AdminDto.ReviewResponse.builder()
                .id(r.getId()).reviewerId(r.getReviewer().getId()).reviewerName(r.getReviewer().getName())
                .targetType(r.getTargetType().name()).targetId(r.getTargetId())
                .rating(r.getRating()).comment(r.getComment()).createdAt(r.getCreatedAt()).build();
    }
}
