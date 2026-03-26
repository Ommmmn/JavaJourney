package com.petymate.service;

import com.petymate.dto.PagedResponse;
import com.petymate.dto.TrainerDto;
import org.springframework.data.domain.Pageable;

public interface TrainingService {
    TrainerDto.SessionOrderResponse createSessionOrder(String email, TrainerDto.CreateSessionOrderRequest request);
    TrainerDto.SessionResponse verifySession(String email, TrainerDto.VerifySessionRequest request);
    PagedResponse<TrainerDto.SessionResponse> getMySessions(String email, Pageable pageable);
    void cancelSession(Long id, String email);
    TrainerDto.SessionOrderResponse buyPackageOrder(Long packageId, String email);
    TrainerDto.UserPackageResponse verifyPackagePurchase(Long packageId, String email, TrainerDto.VerifyPackageRequest request);
    java.util.List<TrainerDto.UserPackageResponse> getMyPackages(String email);
}
