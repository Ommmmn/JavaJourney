package com.petymate.service;

import com.petymate.dto.ProductDto;
import com.petymate.dto.VetDto;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.repository.*;
import com.petymate.service.impl.VetServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VetService Tests")
class VetServiceTest {

    @InjectMocks private VetServiceImpl vetService;
    @Mock private VetRepository vetRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;

    private Vet sampleVet() {
        return Vet.builder()
                .id(1L).name("Dr. Sharma").specialization("Dermatology")
                .qualification("BVSc, MVSc").experienceYears(10)
                .city("Mumbai").state("Maharashtra").phone("9876543210")
                .email("dr.sharma@vet.com").consultationFee(BigDecimal.valueOf(500))
                .availableDays("Mon-Fri").availableHours("10:00-18:00")
                .rating(BigDecimal.valueOf(4.5)).reviewCount(12)
                .totalAppointments(45).isVerified(true).bio("Expert dermatologist for pets")
                .build();
    }

    @Test @DisplayName("Get vets with city filter: returns filtered page")
    void getVets_WithCityFilter() {
        Vet vet = sampleVet();
        Page<Vet> page = new PageImpl<>(List.of(vet), PageRequest.of(0, 12), 1);
        when(vetRepository.findVetsFiltered("Mumbai", null, null, null, null, null, PageRequest.of(0, 12)))
                .thenReturn(page);

        var result = vetService.getVets("Mumbai", null, null, null, null, null, null, PageRequest.of(0, 12));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Dr. Sharma", result.getContent().get(0).getName());
        assertEquals("Mumbai", result.getContent().get(0).getCity());
    }

    @Test @DisplayName("Get vets with specialization filter")
    void getVets_WithSpecializationFilter() {
        Vet vet = sampleVet();
        Page<Vet> page = new PageImpl<>(List.of(vet), PageRequest.of(0, 12), 1);
        when(vetRepository.findVetsFiltered(null, null, "Dermatology", null, null, null, PageRequest.of(0, 12)))
                .thenReturn(page);

        var result = vetService.getVets(null, null, "Dermatology", null, null, null, null, PageRequest.of(0, 12));

        assertEquals(1, result.getTotalElements());
        assertEquals("Dermatology", result.getContent().get(0).getSpecialization());
    }

    @Test @DisplayName("Get vets: empty result")
    void getVets_EmptyResult() {
        Page<Vet> page = new PageImpl<>(List.of(), PageRequest.of(0, 12), 0);
        when(vetRepository.findVetsFiltered("Jaipur", null, null, null, null, null, PageRequest.of(0, 12)))
                .thenReturn(page);

        var result = vetService.getVets("Jaipur", null, null, null, null, null, null, PageRequest.of(0, 12));

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test @DisplayName("Get vet by ID: success")
    void getVetById_Success() {
        Vet vet = sampleVet();
        when(vetRepository.findById(1L)).thenReturn(Optional.of(vet));

        VetDto.VetResponse resp = vetService.getVetById(1L);

        assertEquals("Dr. Sharma", resp.getName());
        assertEquals(BigDecimal.valueOf(500), resp.getConsultationFee());
        assertTrue(resp.getIsVerified());
    }

    @Test @DisplayName("Get vet by ID: not found → exception")
    void getVetById_NotFound() {
        when(vetRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> vetService.getVetById(999L));
    }

    @Test @DisplayName("Add review: success")
    void addReview_Success() {
        User user = User.builder().id(1L).email("john@test.com").build();
        Vet vet = sampleVet();

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(vetRepository.findById(1L)).thenReturn(Optional.of(vet));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, Review.TargetType.VET, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
        when(reviewRepository.getAverageRating(Review.TargetType.VET, 1L)).thenReturn(4.5);
        when(reviewRepository.countByTargetTypeAndTargetId(Review.TargetType.VET, 1L)).thenReturn(1L);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);

        assertDoesNotThrow(() -> vetService.addReview(1L, "john@test.com", new ProductDto.ReviewRequest(5, "Excellent vet!")));
        verify(reviewRepository).save(any(Review.class));
        verify(vetRepository, times(2)).findById(1L);
    }

    @Test @DisplayName("Add review: duplicate → exception")
    void addReview_Duplicate() {
        User user = User.builder().id(1L).email("john@test.com").build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(vetRepository.findById(1L)).thenReturn(Optional.of(sampleVet()));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, Review.TargetType.VET, 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> vetService.addReview(1L, "john@test.com", new ProductDto.ReviewRequest(4, "Duplicate")));
    }

    @Test @DisplayName("Add review: user not found → exception")
    void addReview_UserNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> vetService.addReview(1L, "unknown@test.com", new ProductDto.ReviewRequest(3, "Test")));
    }

    @Test @DisplayName("Add review: vet not found → exception")
    void addReview_VetNotFound() {
        User user = User.builder().id(1L).email("john@test.com").build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(vetRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vetService.addReview(999L, "john@test.com", new ProductDto.ReviewRequest(5, "Test")));
    }
}
