package com.petymate.service;

import com.petymate.dto.PagedResponse;
import com.petymate.dto.ProductDto;
import com.petymate.dto.VetDto;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface VetService {
    PagedResponse<VetDto.VetResponse> getVets(String city, String state, String specialization,
            Boolean verifiedOnly, BigDecimal minRating, BigDecimal maxFee, String sortBy, Pageable pageable);
    VetDto.VetResponse getVetById(Long id);
    void addReview(Long vetId, String email, ProductDto.ReviewRequest request);
}
