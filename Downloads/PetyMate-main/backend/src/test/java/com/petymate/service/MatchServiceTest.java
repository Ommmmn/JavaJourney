package com.petymate.service;

import com.petymate.config.RazorpayConfig;
import com.petymate.dto.MatchDto;
import com.petymate.entity.*;
import com.petymate.exception.*;
import com.petymate.repository.*;
import com.petymate.service.impl.MatchServiceImpl;
import com.razorpay.RazorpayClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchService Tests")
class MatchServiceTest {

    @InjectMocks private MatchServiceImpl matchService;
    @Mock private MatchRequestRepository matchRequestRepository;
    @Mock private PetRepository petRepository;
    @Mock private UserRepository userRepository;
    @Mock private RazorpayClient razorpayClient;
    @Mock private RazorpayConfig razorpayConfig;

    private User requester, receiver;
    private Pet requesterPet, receiverPet;

    @BeforeEach
    void setup() {
        requester = User.builder().id(1L).email("req@test.com").name("Requester").subscriptionTier(User.SubscriptionTier.FREE).build();
        receiver = User.builder().id(2L).email("rec@test.com").name("Receiver").build();
        requesterPet = Pet.builder().id(10L).owner(requester).species(Pet.Species.DOG).gender(Pet.Gender.MALE).build();
        receiverPet = Pet.builder().id(20L).owner(receiver).species(Pet.Species.DOG).gender(Pet.Gender.FEMALE).build();
    }

    @Test @DisplayName("Send request: success")
    void sendRequest_Success() {
        MatchDto.MatchRequestDto dto = new MatchDto.MatchRequestDto(10L, 20L, "Hi!");
        when(userRepository.findByEmail("req@test.com")).thenReturn(Optional.of(requester));
        when(petRepository.findById(10L)).thenReturn(Optional.of(requesterPet));
        when(petRepository.findById(20L)).thenReturn(Optional.of(receiverPet));
        when(matchRequestRepository.existsByRequesterPetIdAndReceiverPetId(10L, 20L)).thenReturn(false);
        when(matchRequestRepository.countRequestsSince(eq(1L), any(LocalDateTime.class))).thenReturn(0L);
        when(matchRequestRepository.save(any(MatchRequest.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> matchService.sendRequest("req@test.com", dto));
        verify(matchRequestRepository).save(any(MatchRequest.class));
    }

    @Test @DisplayName("Send request: same gender → exception")
    void sendRequest_SameGender() {
        receiverPet.setGender(Pet.Gender.MALE);
        MatchDto.MatchRequestDto dto = new MatchDto.MatchRequestDto(10L, 20L, null);
        when(userRepository.findByEmail("req@test.com")).thenReturn(Optional.of(requester));
        when(petRepository.findById(10L)).thenReturn(Optional.of(requesterPet));
        when(petRepository.findById(20L)).thenReturn(Optional.of(receiverPet));
        assertThrows(IllegalArgumentException.class, () -> matchService.sendRequest("req@test.com", dto));
    }

    @Test @DisplayName("Send request: different species → exception")
    void sendRequest_DifferentSpecies() {
        receiverPet.setSpecies(Pet.Species.CAT);
        MatchDto.MatchRequestDto dto = new MatchDto.MatchRequestDto(10L, 20L, null);
        when(userRepository.findByEmail("req@test.com")).thenReturn(Optional.of(requester));
        when(petRepository.findById(10L)).thenReturn(Optional.of(requesterPet));
        when(petRepository.findById(20L)).thenReturn(Optional.of(receiverPet));
        assertThrows(IllegalArgumentException.class, () -> matchService.sendRequest("req@test.com", dto));
    }

    @Test @DisplayName("Send request: free user daily limit → exception")
    void sendRequest_DailyLimit() {
        MatchDto.MatchRequestDto dto = new MatchDto.MatchRequestDto(10L, 20L, null);
        when(userRepository.findByEmail("req@test.com")).thenReturn(Optional.of(requester));
        when(petRepository.findById(10L)).thenReturn(Optional.of(requesterPet));
        when(petRepository.findById(20L)).thenReturn(Optional.of(receiverPet));
        when(matchRequestRepository.existsByRequesterPetIdAndReceiverPetId(10L, 20L)).thenReturn(false);
        when(matchRequestRepository.countRequestsSince(eq(1L), any(LocalDateTime.class))).thenReturn(2L);
        assertThrows(SubscriptionRequiredException.class, () -> matchService.sendRequest("req@test.com", dto));
    }

    @Test @DisplayName("Accept request: success")
    void acceptRequest_Success() {
        MatchRequest mr = MatchRequest.builder().id(1L).receiver(receiver).status(MatchRequest.MatchStatus.PENDING).build();
        when(matchRequestRepository.findById(1L)).thenReturn(Optional.of(mr));
        when(matchRequestRepository.save(any())).thenReturn(mr);
        assertDoesNotThrow(() -> matchService.acceptRequest(1L, "rec@test.com"));
        assertEquals(MatchRequest.MatchStatus.ACCEPTED, mr.getStatus());
    }

    @Test @DisplayName("Accept request: not receiver → exception")
    void acceptRequest_NotReceiver() {
        MatchRequest mr = MatchRequest.builder().id(1L).receiver(receiver).build();
        when(matchRequestRepository.findById(1L)).thenReturn(Optional.of(mr));
        assertThrows(UnauthorizedException.class, () -> matchService.acceptRequest(1L, "wrong@test.com"));
    }
}
