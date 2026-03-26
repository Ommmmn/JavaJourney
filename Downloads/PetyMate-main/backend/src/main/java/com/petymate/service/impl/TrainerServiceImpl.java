package com.petymate.service.impl;

import com.petymate.dto.*;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.repository.*;
import com.petymate.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingPackageRepository packageRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    public PagedResponse<TrainerDto.TrainerResponse> getTrainers(String city, String state, String specialization,
            String speciesExpertise, Boolean verifiedOnly, BigDecimal minRating, BigDecimal maxFee, String sortBy, Pageable pageable) {
        Trainer.TrainerSpecialization spec = specialization != null ? Trainer.TrainerSpecialization.valueOf(specialization) : null;
        Page<Trainer> page = trainerRepository.findTrainersFiltered(city, state, spec, speciesExpertise, verifiedOnly, minRating, maxFee, pageable);
        return PagedResponse.<TrainerDto.TrainerResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override
    public TrainerDto.TrainerResponse getTrainerById(Long id) {
        return mapToResponse(trainerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trainer not found")));
    }

    @Override @Transactional
    public void addReview(Long trainerId, String email, ProductDto.ReviewRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        trainerRepository.findById(trainerId).orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        if (reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(user.getId(), Review.TargetType.TRAINER, trainerId))
            throw new IllegalArgumentException("Already reviewed");
        reviewRepository.save(Review.builder().reviewer(user).targetType(Review.TargetType.TRAINER).targetId(trainerId)
                .rating(request.getRating()).comment(request.getComment()).build());
        Trainer t = trainerRepository.findById(trainerId).orElseThrow();
        t.setRating(BigDecimal.valueOf(reviewRepository.getAverageRating(Review.TargetType.TRAINER, trainerId)).setScale(2, RoundingMode.HALF_UP));
        t.setReviewCount((int) reviewRepository.countByTargetTypeAndTargetId(Review.TargetType.TRAINER, trainerId));
        trainerRepository.save(t);
    }

    private TrainerDto.TrainerResponse mapToResponse(Trainer t) {
        var packages = packageRepository.findByTrainerIdAndIsActiveTrue(t.getId()).stream()
                .map(p -> TrainerDto.TrainingPackageDto.builder()
                        .id(p.getId()).name(p.getName()).description(p.getDescription())
                        .sessionsCount(p.getSessionsCount()).price(p.getPrice()).validityDays(p.getValidityDays()).build())
                .collect(Collectors.toList());
        return TrainerDto.TrainerResponse.builder()
                .id(t.getId()).name(t.getName()).specialization(t.getSpecialization().name())
                .speciesExpertise(t.getSpeciesExpertise()).experienceYears(t.getExperienceYears())
                .certification(t.getCertification()).bio(t.getBio()).city(t.getCity()).state(t.getState())
                .phone(t.getPhone()).email(t.getEmail()).photoUrl(t.getPhotoUrl())
                .sessionFeePerHour(t.getSessionFeePerHour()).sessionModes(t.getSessionModes())
                .availableDays(t.getAvailableDays()).availableHours(t.getAvailableHours())
                .rating(t.getRating()).reviewCount(t.getReviewCount()).totalSessions(t.getTotalSessions())
                .isVerified(t.getIsVerified()).packages(packages).build();
    }
}
