package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.VetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/vets")
@RequiredArgsConstructor
public class VetController {

    private final VetService vetService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<VetDto.VetResponse>>> getVets(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Boolean verifiedOnly,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) BigDecimal maxFee,
            @RequestParam(defaultValue = "RATING") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Sort sort = switch (sortBy) {
            case "FEE_ASC" -> Sort.by("consultationFee").ascending();
            case "FEE_DESC" -> Sort.by("consultationFee").descending();
            case "EXPERIENCE" -> Sort.by("experienceYears").descending();
            default -> Sort.by("rating").descending();
        };
        return ResponseEntity.ok(ApiResponse.success(vetService.getVets(city, state, specialization,
                verifiedOnly, minRating, maxFee, sortBy, PageRequest.of(page, size, sort))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VetDto.VetResponse>> getVet(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(vetService.getVetById(id)));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ApiResponse<Void>> addReview(@PathVariable Long id, Authentication auth,
            @Valid @RequestBody ProductDto.ReviewRequest request) {
        vetService.addReview(id, auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Review added", null));
    }
}
