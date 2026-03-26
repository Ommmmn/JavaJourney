package com.petymate.service.impl;

import com.petymate.config.RazorpayConfig;
import com.petymate.dto.MatchDto;
import com.petymate.dto.PagedResponse;
import com.petymate.dto.PetDto;
import com.petymate.entity.*;
import com.petymate.exception.*;
import com.petymate.repository.*;
import com.petymate.service.MatchService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchServiceImpl implements MatchService {

    private final MatchRequestRepository matchRequestRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    @Override
    @Transactional
    public void sendRequest(String email, MatchDto.MatchRequestDto request) {
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Pet requesterPet = petRepository.findById(request.getRequesterPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Requester pet not found"));
        Pet receiverPet = petRepository.findById(request.getReceiverPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver pet not found"));

        if (!requesterPet.getOwner().getId().equals(requester.getId())) {
            throw new UnauthorizedException("You can only send requests from your own pets");
        }
        if (requesterPet.getSpecies() != receiverPet.getSpecies()) {
            throw new IllegalArgumentException("Pets must be the same species for matching");
        }
        if (requesterPet.getGender() == receiverPet.getGender()) {
            throw new IllegalArgumentException("Mating requires opposite genders");
        }
        if (matchRequestRepository.existsByRequesterPetIdAndReceiverPetId(
                request.getRequesterPetId(), request.getReceiverPetId())) {
            throw new IllegalArgumentException("Match request already exists for these pets");
        }

        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        long todayCount = matchRequestRepository.countRequestsSince(requester.getId(), todayStart);

        if (requester.getSubscriptionTier() == User.SubscriptionTier.FREE && todayCount >= 2) {
            throw new SubscriptionRequiredException("Free users can send max 2 requests/day. Upgrade your plan!");
        }
        if (requester.getSubscriptionTier() == User.SubscriptionTier.BASIC && todayCount >= 10) {
            throw new SubscriptionRequiredException("Basic users can send max 10 requests/day. Upgrade to Premium!");
        }

        MatchRequest matchRequest = MatchRequest.builder()
                .requester(requester)
                .receiver(receiverPet.getOwner())
                .requesterPet(requesterPet)
                .receiverPet(receiverPet)
                .message(request.getMessage())
                .status(MatchRequest.MatchStatus.PENDING)
                .unlocked(false)
                .build();

        matchRequestRepository.save(matchRequest);
        log.info("Match request sent from pet {} to pet {}", requesterPet.getId(), receiverPet.getId());
    }

    @Override
    public PagedResponse<MatchDto.MatchResponse> getReceivedRequests(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<MatchRequest> page = matchRequestRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId(), pageable);
        return mapToPagedResponse(page);
    }

    @Override
    public PagedResponse<MatchDto.MatchResponse> getSentRequests(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<MatchRequest> page = matchRequestRepository.findByRequesterIdOrderByCreatedAtDesc(user.getId(), pageable);
        return mapToPagedResponse(page);
    }

    @Override
    @Transactional
    public void acceptRequest(Long id, String email) {
        MatchRequest mr = matchRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match request not found"));
        if (!mr.getReceiver().getEmail().equals(email)) {
            throw new UnauthorizedException("Only the receiver can accept this request");
        }
        mr.setStatus(MatchRequest.MatchStatus.ACCEPTED);
        matchRequestRepository.save(mr);
    }

    @Override
    @Transactional
    public void rejectRequest(Long id, String email) {
        MatchRequest mr = matchRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match request not found"));
        if (!mr.getReceiver().getEmail().equals(email)) {
            throw new UnauthorizedException("Only the receiver can reject this request");
        }
        mr.setStatus(MatchRequest.MatchStatus.REJECTED);
        matchRequestRepository.save(mr);
    }

    @Override
    public MatchDto.UnlockOrderResponse createUnlockOrder(Long id, String email) {
        matchRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match request not found"));
        try {
            JSONObject options = new JSONObject();
            options.put("amount", 9900);
            options.put("currency", "INR");
            options.put("receipt", "unlock_" + UUID.randomUUID());
            Order order = razorpayClient.orders.create(options);
            return MatchDto.UnlockOrderResponse.builder()
                    .orderId(order.get("id"))
                    .amount(9900)
                    .currency("INR")
                    .keyId(razorpayConfig.getKeyId())
                    .build();
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }
    }

    @Override
    @Transactional
    public MatchDto.OwnerContactResponse unlockContact(Long id, String email, MatchDto.UnlockRequest request) {
        MatchRequest mr = matchRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match request not found"));

        String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
        String expectedSig = new HmacUtils("HmacSHA256", razorpayConfig.getKeySecret()).hmacHex(payload);

        if (!expectedSig.equals(request.getRazorpaySignature())) {
            throw new PaymentVerificationException("Payment signature verification failed");
        }

        mr.setUnlocked(true);
        mr.setUnlockPaymentId(request.getRazorpayPaymentId());
        matchRequestRepository.save(mr);

        User owner;
        if (mr.getRequester().getEmail().equals(email)) {
            owner = mr.getReceiver();
        } else {
            owner = mr.getRequester();
        }

        return MatchDto.OwnerContactResponse.builder()
                .ownerName(owner.getName())
                .ownerPhone(owner.getPhone())
                .ownerEmail(owner.getEmail())
                .ownerCity(owner.getCity())
                .build();
    }

    private PagedResponse<MatchDto.MatchResponse> mapToPagedResponse(Page<MatchRequest> page) {
        List<MatchDto.MatchResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PagedResponse.<MatchDto.MatchResponse>builder()
                .content(content).page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    private MatchDto.MatchResponse mapToResponse(MatchRequest mr) {
        return MatchDto.MatchResponse.builder()
                .id(mr.getId())
                .requesterId(mr.getRequester().getId())
                .requesterName(mr.getRequester().getName())
                .receiverId(mr.getReceiver().getId())
                .receiverName(mr.getReceiver().getName())
                .status(mr.getStatus().name())
                .message(mr.getMessage())
                .unlocked(mr.getUnlocked())
                .createdAt(mr.getCreatedAt())
                .build();
    }
}
