package com.petymate.service;

import com.petymate.dto.PagedResponse;
import com.petymate.dto.ProductDto;
import com.petymate.dto.TrainerDto;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface TrainerService {
    PagedResponse<TrainerDto.TrainerResponse> getTrainers(String city, String state, String specialization,
            String speciesExpertise, Boolean verifiedOnly, BigDecimal minRating, BigDecimal maxFee, String sortBy, Pageable pageable);
    TrainerDto.TrainerResponse getTrainerById(Long id);
    void addReview(Long trainerId, String email, ProductDto.ReviewRequest request);
}
