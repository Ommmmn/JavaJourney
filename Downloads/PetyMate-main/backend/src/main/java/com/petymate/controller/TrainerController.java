package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TrainerDto.TrainerResponse>>> getTrainers(
            @RequestParam(required = false) String city, @RequestParam(required = false) String state,
            @RequestParam(required = false) String specialization, @RequestParam(required = false) String speciesExpertise,
            @RequestParam(required = false) Boolean verifiedOnly, @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) BigDecimal maxFee, @RequestParam(defaultValue = "RATING") String sortBy,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size) {
        Sort sort = switch (sortBy) {
            case "FEE_ASC" -> Sort.by("sessionFeePerHour").ascending();
            case "FEE_DESC" -> Sort.by("sessionFeePerHour").descending();
            case "EXPERIENCE" -> Sort.by("experienceYears").descending();
            case "TOTAL_SESSIONS" -> Sort.by("totalSessions").descending();
            default -> Sort.by("rating").descending();
        };
        return ResponseEntity.ok(ApiResponse.success(trainerService.getTrainers(city, state, specialization,
                speciesExpertise, verifiedOnly, minRating, maxFee, sortBy, PageRequest.of(page, size, sort))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainerDto.TrainerResponse>> getTrainer(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(trainerService.getTrainerById(id)));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ApiResponse<Void>> addReview(@PathVariable Long id, Authentication auth,
            @Valid @RequestBody ProductDto.ReviewRequest request) {
        trainerService.addReview(id, auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Review added", null));
    }
}
