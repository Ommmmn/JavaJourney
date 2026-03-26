package com.petymate.service;

import com.petymate.dto.MatchDto;
import com.petymate.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface MatchService {
    void sendRequest(String email, MatchDto.MatchRequestDto request);
    PagedResponse<MatchDto.MatchResponse> getReceivedRequests(String email, Pageable pageable);
    PagedResponse<MatchDto.MatchResponse> getSentRequests(String email, Pageable pageable);
    void acceptRequest(Long id, String email);
    void rejectRequest(Long id, String email);
    MatchDto.UnlockOrderResponse createUnlockOrder(Long id, String email);
    MatchDto.OwnerContactResponse unlockContact(Long id, String email, MatchDto.UnlockRequest request);
}
