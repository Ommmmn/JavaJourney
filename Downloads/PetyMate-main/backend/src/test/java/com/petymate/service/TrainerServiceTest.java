package com.petymate.service;

import com.petymate.dto.ProductDto;
import com.petymate.dto.TrainerDto;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.repository.*;
import com.petymate.service.impl.TrainerServiceImpl;
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
@DisplayName("TrainerService Tests")
class TrainerServiceTest {

    @InjectMocks private TrainerServiceImpl trainerService;
    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingPackageRepository packageRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;

    private Trainer sampleTrainer() {
        return Trainer.builder()
                .id(1L).name("Vikram Singh")
                .specialization(Trainer.TrainerSpecialization.OBEDIENCE)
                .speciesExpertise("DOG,CAT").experienceYears(8)
                .certification("CPDT-KA Certified").bio("Professional dog trainer")
                .city("Delhi").state("Delhi").phone("9876543210")
                .email("vikram@trainer.com").sessionFeePerHour(BigDecimal.valueOf(800))
                .sessionModes("HOME_VISIT,TRAINING_CENTER,ONLINE")
                .availableDays("Mon-Sat").availableHours("8:00-18:00")
                .rating(BigDecimal.valueOf(4.7)).reviewCount(25)
                .totalSessions(120).isVerified(true)
                .build();
    }

    @Test @DisplayName("Get trainers with species expertise filter")
    void getTrainers_WithSpeciesFilter() {
        Trainer trainer = sampleTrainer();
        Page<Trainer> page = new PageImpl<>(List.of(trainer), PageRequest.of(0, 12), 1);
        when(trainerRepository.findTrainersFiltered(null, null, null, "DOG", null, null, null, PageRequest.of(0, 12)))
                .thenReturn(page);
        when(packageRepository.findByTrainerIdAndIsActiveTrue(1L)).thenReturn(List.of());

        var result = trainerService.getTrainers(null, null, null, "DOG", null, null, null, null, PageRequest.of(0, 12));

        assertEquals(1, result.getContent().size());
        assertEquals("Vikram Singh", result.getContent().get(0).getName());
        assertTrue(result.getContent().get(0).getSpeciesExpertise().contains("DOG"));
    }

    @Test @DisplayName("Get trainers with session mode filter")
    void getTrainers_WithModeFilter() {
        Trainer trainer = sampleTrainer();
        Page<Trainer> page = new PageImpl<>(List.of(trainer), PageRequest.of(0, 12), 1);
        when(trainerRepository.findTrainersFiltered("Delhi", null, Trainer.TrainerSpecialization.OBEDIENCE, null, null, null, null, PageRequest.of(0, 12)))
                .thenReturn(page);
        when(packageRepository.findByTrainerIdAndIsActiveTrue(1L)).thenReturn(List.of());

        var result = trainerService.getTrainers("Delhi", null, "OBEDIENCE", null, null, null, null, null, PageRequest.of(0, 12));

        assertEquals(1, result.getTotalElements());
        assertEquals("OBEDIENCE", result.getContent().get(0).getSpecialization());
    }

    @Test @DisplayName("Get trainers: empty result")
    void getTrainers_EmptyResult() {
        Page<Trainer> page = new PageImpl<>(List.of(), PageRequest.of(0, 12), 0);
        when(trainerRepository.findTrainersFiltered(null, null, null, null, null, null, null, PageRequest.of(0, 12)))
                .thenReturn(page);

        var result = trainerService.getTrainers(null, null, null, null, null, null, null, null, PageRequest.of(0, 12));

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test @DisplayName("Get trainer by ID: success")
    void getTrainerById_Success() {
        Trainer trainer = sampleTrainer();
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(packageRepository.findByTrainerIdAndIsActiveTrue(1L)).thenReturn(List.of());

        TrainerDto.TrainerResponse resp = trainerService.getTrainerById(1L);

        assertEquals("Vikram Singh", resp.getName());
        assertEquals(BigDecimal.valueOf(800), resp.getSessionFeePerHour());
        assertTrue(resp.getIsVerified());
        assertNotNull(resp.getPackages());
    }

    @Test @DisplayName("Get trainer by ID: not found → exception")
    void getTrainerById_NotFound() {
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> trainerService.getTrainerById(999L));
    }

    @Test @DisplayName("Get trainer by ID: with packages")
    void getTrainerById_WithPackages() {
        Trainer trainer = sampleTrainer();
        TrainingPackage pkg = TrainingPackage.builder()
                .id(1L).trainer(trainer).name("8-Session Obedience Pack")
                .description("Complete obedience training").sessionsCount(8)
                .price(BigDecimal.valueOf(5500)).validityDays(60).isActive(true).build();

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(packageRepository.findByTrainerIdAndIsActiveTrue(1L)).thenReturn(List.of(pkg));

        TrainerDto.TrainerResponse resp = trainerService.getTrainerById(1L);

        assertEquals(1, resp.getPackages().size());
        assertEquals("8-Session Obedience Pack", resp.getPackages().get(0).getName());
        assertEquals(8, resp.getPackages().get(0).getSessionsCount());
        assertEquals(BigDecimal.valueOf(5500), resp.getPackages().get(0).getPrice());
    }

    @Test @DisplayName("Add review: success")
    void addReview_Success() {
        User user = User.builder().id(1L).email("john@test.com").build();
        Trainer trainer = sampleTrainer();

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, Review.TargetType.TRAINER, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
        when(reviewRepository.getAverageRating(Review.TargetType.TRAINER, 1L)).thenReturn(4.8);
        when(reviewRepository.countByTargetTypeAndTargetId(Review.TargetType.TRAINER, 1L)).thenReturn(1L);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        assertDoesNotThrow(() -> trainerService.addReview(1L, "john@test.com", new ProductDto.ReviewRequest(5, "Excellent trainer!")));
        verify(reviewRepository).save(any(Review.class));
    }

    @Test @DisplayName("Add review: duplicate → exception")
    void addReview_Duplicate() {
        User user = User.builder().id(1L).email("john@test.com").build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(sampleTrainer()));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, Review.TargetType.TRAINER, 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.addReview(1L, "john@test.com", new ProductDto.ReviewRequest(4, "Duplicate")));
    }

    @Test @DisplayName("Add review: trainer not found → exception")
    void addReview_TrainerNotFound() {
        User user = User.builder().id(1L).email("john@test.com").build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> trainerService.addReview(999L, "john@test.com", new ProductDto.ReviewRequest(5, "Test")));
    }
}
