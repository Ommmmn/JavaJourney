package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/training")
@RequiredArgsConstructor
public class TrainingSessionController {

    private final TrainingService trainingService;

    @PostMapping("/sessions/create-order")
    public ResponseEntity<ApiResponse<TrainerDto.SessionOrderResponse>> createSessionOrder(
            Authentication auth, @Valid @RequestBody TrainerDto.CreateSessionOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(trainingService.createSessionOrder(auth.getName(), request)));
    }

    @PostMapping("/sessions/verify")
    public ResponseEntity<ApiResponse<TrainerDto.SessionResponse>> verifySession(
            Authentication auth, @Valid @RequestBody TrainerDto.VerifySessionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Session booked!", trainingService.verifySession(auth.getName(), request)));
    }

    @GetMapping("/sessions/my")
    public ResponseEntity<ApiResponse<PagedResponse<TrainerDto.SessionResponse>>> getMySessions(
            Authentication auth, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(trainingService.getMySessions(auth.getName(), PageRequest.of(page, size))));
    }

    @PutMapping("/sessions/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSession(@PathVariable Long id, Authentication auth) {
        trainingService.cancelSession(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Session cancelled", null));
    }

    @PostMapping("/packages/{packageId}/buy-order")
    public ResponseEntity<ApiResponse<TrainerDto.SessionOrderResponse>> buyPackageOrder(
            @PathVariable Long packageId, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(trainingService.buyPackageOrder(packageId, auth.getName())));
    }

    @PostMapping("/packages/{packageId}/buy-verify")
    public ResponseEntity<ApiResponse<TrainerDto.UserPackageResponse>> verifyPackage(
            @PathVariable Long packageId, Authentication auth, @Valid @RequestBody TrainerDto.VerifyPackageRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Package purchased!", trainingService.verifyPackagePurchase(packageId, auth.getName(), request)));
    }

    @GetMapping("/packages/my")
    public ResponseEntity<ApiResponse<List<TrainerDto.UserPackageResponse>>> getMyPackages(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(trainingService.getMyPackages(auth.getName())));
    }
}
