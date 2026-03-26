package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Void>> sendRequest(
            Authentication auth, @Valid @RequestBody MatchDto.MatchRequestDto request) {
        matchService.sendRequest(auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Match request sent", null));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<PagedResponse<MatchDto.MatchResponse>>> getReceived(
            Authentication auth, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(matchService.getReceivedRequests(auth.getName(), PageRequest.of(page, size))));
    }

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<PagedResponse<MatchDto.MatchResponse>>> getSent(
            Authentication auth, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(matchService.getSentRequests(auth.getName(), PageRequest.of(page, size))));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<Void>> accept(@PathVariable Long id, Authentication auth) {
        matchService.acceptRequest(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Match request accepted", null));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(@PathVariable Long id, Authentication auth) {
        matchService.rejectRequest(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Match request rejected", null));
    }

    @PostMapping("/{id}/create-unlock-order")
    public ResponseEntity<ApiResponse<MatchDto.UnlockOrderResponse>> createUnlockOrder(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(matchService.createUnlockOrder(id, auth.getName())));
    }

    @PostMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<MatchDto.OwnerContactResponse>> unlock(
            @PathVariable Long id, Authentication auth, @Valid @RequestBody MatchDto.UnlockRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Contact unlocked!", matchService.unlockContact(id, auth.getName(), request)));
    }
}
