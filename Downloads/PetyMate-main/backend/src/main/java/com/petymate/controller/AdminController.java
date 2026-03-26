package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDto.DashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboard()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PagedResponse<AuthDto.UserDto>>> getUsers(
            @RequestParam(required = false) String role, @RequestParam(required = false) String tier,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsers(role, tier, banned,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(@PathVariable Long id) {
        adminService.banUser(id); return ResponseEntity.ok(ApiResponse.success("User banned", null));
    }

    @PutMapping("/users/{id}/unban")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable Long id) {
        adminService.unbanUser(id); return ResponseEntity.ok(ApiResponse.success("User unbanned", null));
    }

    @PutMapping("/users/{id}/tier")
    public ResponseEntity<ApiResponse<Void>> updateTier(@PathVariable Long id,
            @Valid @RequestBody AdminDto.UpdateTierRequest request) {
        adminService.updateTier(id, request); return ResponseEntity.ok(ApiResponse.success("Tier updated", null));
    }

    @PutMapping("/vets/{id}/verify")
    public ResponseEntity<ApiResponse<Void>> verifyVet(@PathVariable Long id) {
        adminService.verifyVet(id); return ResponseEntity.ok(ApiResponse.success("Vet verified", null));
    }

    @PutMapping("/trainers/{id}/verify")
    public ResponseEntity<ApiResponse<Void>> verifyTrainer(@PathVariable Long id) {
        adminService.verifyTrainer(id); return ResponseEntity.ok(ApiResponse.success("Trainer verified", null));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(@PathVariable Long id,
            @Valid @RequestBody AdminDto.UpdateOrderStatusRequest request) {
        adminService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", null));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<PagedResponse<AdminDto.ReviewResponse>>> getReviews(
            @RequestParam String targetType, @RequestParam Long targetId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getReviews(targetType, targetId, PageRequest.of(page, size))));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id); return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }
}
