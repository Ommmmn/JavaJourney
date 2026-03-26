package com.petymate.service.impl;

import com.petymate.config.RazorpayConfig;
import com.petymate.dto.PagedResponse;
import com.petymate.dto.TrainerDto;
import com.petymate.entity.*;
import com.petymate.exception.*;
import com.petymate.repository.*;
import com.petymate.service.TrainingService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class TrainingServiceImpl implements TrainingService {

    private final TrainingSessionRepository sessionRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingPackageRepository packageRepository;
    private final UserPackageRepository userPackageRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    @Override @Transactional
    public TrainerDto.SessionOrderResponse createSessionOrder(String email, TrainerDto.CreateSessionOrderRequest req) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Trainer trainer = trainerRepository.findById(req.getTrainerId()).orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        petRepository.findById(req.getPetId()).orElseThrow(() -> new ResourceNotFoundException("Pet not found"));

        if (sessionRepository.existsByTrainerIdAndSessionDateAndSessionTimeAndStatusNot(
                req.getTrainerId(), req.getDate(), req.getTime(), TrainingSession.SessionStatus.CANCELLED))
            throw new IllegalArgumentException("Trainer not available at this time");

        BigDecimal totalFee = trainer.getSessionFeePerHour().multiply(BigDecimal.valueOf(req.getDurationHours()));
        int amountPaise = totalFee.multiply(BigDecimal.valueOf(100)).intValue();

        try {
            JSONObject options = new JSONObject();
            options.put("amount", amountPaise); options.put("currency", "INR");
            options.put("receipt", "train_" + UUID.randomUUID());
            com.razorpay.Order order = razorpayClient.orders.create(options);

            TrainingSession session = TrainingSession.builder()
                    .user(user).trainer(trainer).pet(petRepository.findById(req.getPetId()).orElseThrow())
                    .sessionDate(req.getDate()).sessionTime(req.getTime()).durationHours(req.getDurationHours())
                    .mode(TrainingSession.SessionMode.valueOf(req.getMode()))
                    .focusArea(req.getFocusArea()).petCurrentIssues(req.getPetCurrentIssues())
                    .totalFee(totalFee).razorpayOrderId(order.get("id"))
                    .status(TrainingSession.SessionStatus.PENDING).build();
            sessionRepository.save(session);

            return TrainerDto.SessionOrderResponse.builder()
                    .razorpayOrderId(order.get("id")).amount(amountPaise).currency("INR")
                    .keyId(razorpayConfig.getKeyId()).totalFee(totalFee).build();
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create training order", e);
        }
    }

    @Override @Transactional
    public TrainerDto.SessionResponse verifySession(String email, TrainerDto.VerifySessionRequest req) {
        String payload = req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId();
        String expected = new HmacUtils("HmacSHA256", razorpayConfig.getKeySecret()).hmacHex(payload);
        if (!expected.equals(req.getRazorpaySignature())) throw new PaymentVerificationException("Invalid signature");

        TrainingSession session = sessionRepository.findByRazorpayOrderId(req.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        session.setRazorpayPaymentId(req.getRazorpayPaymentId());
        session.setRazorpaySignature(req.getRazorpaySignature());
        session.setStatus(TrainingSession.SessionStatus.CONFIRMED);
        sessionRepository.save(session);

        Trainer t = session.getTrainer();
        t.setTotalSessions(t.getTotalSessions() + 1);
        trainerRepository.save(t);

        return mapToSessionResponse(session);
    }

    @Override
    public PagedResponse<TrainerDto.SessionResponse> getMySessions(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<TrainingSession> page = sessionRepository.findByUserIdOrderBySessionDateDesc(user.getId(), pageable);
        return PagedResponse.<TrainerDto.SessionResponse>builder()
                .content(page.getContent().stream().map(this::mapToSessionResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override @Transactional
    public void cancelSession(Long id, String email) {
        TrainingSession s = sessionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        if (!s.getUser().getEmail().equals(email)) throw new UnauthorizedException("Not your session");
        if (LocalDateTime.of(s.getSessionDate(), s.getSessionTime()).isBefore(LocalDateTime.now().plusHours(24)))
            throw new IllegalArgumentException("Cannot cancel within 24 hours");
        s.setStatus(TrainingSession.SessionStatus.CANCELLED);
        sessionRepository.save(s);
    }

    @Override
    public TrainerDto.SessionOrderResponse buyPackageOrder(Long packageId, String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TrainingPackage pkg = packageRepository.findById(packageId).orElseThrow(() -> new ResourceNotFoundException("Package not found"));
        int amountPaise = pkg.getPrice().multiply(BigDecimal.valueOf(100)).intValue();
        try {
            JSONObject options = new JSONObject();
            options.put("amount", amountPaise); options.put("currency", "INR");
            options.put("receipt", "pkg_" + UUID.randomUUID());
            com.razorpay.Order order = razorpayClient.orders.create(options);
            return TrainerDto.SessionOrderResponse.builder()
                    .razorpayOrderId(order.get("id")).amount(amountPaise).currency("INR")
                    .keyId(razorpayConfig.getKeyId()).totalFee(pkg.getPrice()).build();
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create package order", e);
        }
    }

    @Override @Transactional
    public TrainerDto.UserPackageResponse verifyPackagePurchase(Long packageId, String email, TrainerDto.VerifyPackageRequest req) {
        String payload = req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId();
        String expected = new HmacUtils("HmacSHA256", razorpayConfig.getKeySecret()).hmacHex(payload);
        if (!expected.equals(req.getRazorpaySignature())) throw new PaymentVerificationException("Invalid signature");

        User user = userRepository.findByEmail(email).orElseThrow();
        TrainingPackage pkg = packageRepository.findById(packageId).orElseThrow();

        UserPackage up = UserPackage.builder()
                .user(user).trainingPackage(pkg).trainer(pkg.getTrainer())
                .sessionsRemaining(pkg.getSessionsCount())
                .razorpayPaymentId(req.getRazorpayPaymentId())
                .expiresAt(LocalDate.now().plusDays(pkg.getValidityDays())).build();
        userPackageRepository.save(up);

        return TrainerDto.UserPackageResponse.builder()
                .id(up.getId()).trainerName(pkg.getTrainer().getName())
                .sessionsRemaining(up.getSessionsRemaining()).expiresAt(up.getExpiresAt())
                .purchasedAt(up.getPurchasedAt())
                .packageInfo(TrainerDto.TrainingPackageDto.builder()
                        .id(pkg.getId()).name(pkg.getName()).sessionsCount(pkg.getSessionsCount())
                        .price(pkg.getPrice()).validityDays(pkg.getValidityDays()).build())
                .build();
    }

    @Override
    public List<TrainerDto.UserPackageResponse> getMyPackages(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return userPackageRepository.findByUserIdOrderByPurchasedAtDesc(user.getId()).stream()
                .map(up -> TrainerDto.UserPackageResponse.builder()
                        .id(up.getId()).trainerName(up.getTrainer().getName())
                        .sessionsRemaining(up.getSessionsRemaining()).expiresAt(up.getExpiresAt())
                        .purchasedAt(up.getPurchasedAt())
                        .packageInfo(TrainerDto.TrainingPackageDto.builder()
                                .id(up.getTrainingPackage().getId()).name(up.getTrainingPackage().getName())
                                .sessionsCount(up.getTrainingPackage().getSessionsCount())
                                .price(up.getTrainingPackage().getPrice())
                                .validityDays(up.getTrainingPackage().getValidityDays()).build())
                        .build())
                .collect(Collectors.toList());
    }

    private TrainerDto.SessionResponse mapToSessionResponse(TrainingSession s) {
        return TrainerDto.SessionResponse.builder()
                .id(s.getId()).userId(s.getUser().getId())
                .sessionDate(s.getSessionDate()).sessionTime(s.getSessionTime())
                .durationHours(s.getDurationHours()).mode(s.getMode().name())
                .focusArea(s.getFocusArea()).petCurrentIssues(s.getPetCurrentIssues())
                .status(s.getStatus().name()).totalFee(s.getTotalFee())
                .trainerNotes(s.getTrainerNotes()).createdAt(s.getCreatedAt()).build();
    }
}
