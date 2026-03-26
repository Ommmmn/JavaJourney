package com.petymate.service.impl;

import com.petymate.dto.*;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.repository.*;
import com.petymate.service.VetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    public PagedResponse<VetDto.VetResponse> getVets(String city, String state, String specialization,
            Boolean verifiedOnly, BigDecimal minRating, BigDecimal maxFee, String sortBy, Pageable pageable) {
        Page<Vet> page = vetRepository.findVetsFiltered(city, state, specialization, verifiedOnly, minRating, maxFee, pageable);
        return PagedResponse.<VetDto.VetResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override
    public VetDto.VetResponse getVetById(Long id) {
        return mapToResponse(vetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vet not found")));
    }

    @Override @Transactional
    public void addReview(Long vetId, String email, ProductDto.ReviewRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        vetRepository.findById(vetId).orElseThrow(() -> new ResourceNotFoundException("Vet not found"));
        if (reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(user.getId(), Review.TargetType.VET, vetId))
            throw new IllegalArgumentException("Already reviewed");
        reviewRepository.save(Review.builder().reviewer(user).targetType(Review.TargetType.VET).targetId(vetId)
                .rating(request.getRating()).comment(request.getComment()).build());
        Vet vet = vetRepository.findById(vetId).orElseThrow();
        vet.setRating(BigDecimal.valueOf(reviewRepository.getAverageRating(Review.TargetType.VET, vetId)).setScale(2, RoundingMode.HALF_UP));
        vet.setReviewCount((int) reviewRepository.countByTargetTypeAndTargetId(Review.TargetType.VET, vetId));
        vetRepository.save(vet);
    }

    private VetDto.VetResponse mapToResponse(Vet v) {
        return VetDto.VetResponse.builder()
                .id(v.getId()).name(v.getName()).specialization(v.getSpecialization())
                .qualification(v.getQualification()).experienceYears(v.getExperienceYears())
                .city(v.getCity()).state(v.getState()).phone(v.getPhone()).email(v.getEmail())
                .photoUrl(v.getPhotoUrl()).consultationFee(v.getConsultationFee())
                .availableDays(v.getAvailableDays()).availableHours(v.getAvailableHours())
                .rating(v.getRating()).reviewCount(v.getReviewCount()).totalAppointments(v.getTotalAppointments())
                .isVerified(v.getIsVerified()).bio(v.getBio()).build();
    }
}
