package com.petymate.service;

import com.petymate.dto.PetDto;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.exception.UnauthorizedException;
import com.petymate.repository.*;
import com.petymate.service.impl.PetServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Tests")
class PetServiceTest {

    @InjectMocks private PetServiceImpl petService;
    @Mock private PetRepository petRepository;
    @Mock private PetPhotoRepository petPhotoRepository;
    @Mock private UserRepository userRepository;
    @Mock private MatchRequestRepository matchRequestRepository;

    private User testUser;
    private Pet testPet;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(petService, "uploadDir", "./uploads");
        ReflectionTestUtils.setField(petService, "baseUrl", "http://localhost:8080");
        testUser = User.builder().id(1L).email("john@test.com").name("John").phone("9876543210").build();
        testPet = Pet.builder().id(1L).owner(testUser).name("Buddy").species(Pet.Species.DOG)
                .breed("Labrador").ageMonths(24).gender(Pet.Gender.MALE)
                .listingType(Pet.ListingType.MATING).city("Mumbai").state("MH").pincode("400001")
                .status(Pet.PetStatus.ACTIVE).build();
    }

    @Test @DisplayName("Create pet: success")
    void createPet_Success() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testUser));
        when(petRepository.save(any(Pet.class))).thenAnswer(i -> { Pet p = i.getArgument(0); p.setId(1L); return p; });
        when(petPhotoRepository.findByPetIdOrderByOrderIndex(anyLong())).thenReturn(Collections.emptyList());

        PetDto.CreatePetRequest req = new PetDto.CreatePetRequest();
        req.setName("Buddy"); req.setSpecies("DOG"); req.setBreed("Labrador"); req.setGender("MALE");
        req.setListingType("MATING"); req.setCity("Mumbai"); req.setPincode("400001");

        PetDto.PetResponse resp = petService.createPet("john@test.com", req, null);

        assertNotNull(resp);
        assertEquals("Buddy", resp.getName());
        assertEquals("DOG", resp.getSpecies());
    }

    @Test @DisplayName("Get pet by ID: success")
    void getPetById_Success() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petPhotoRepository.findByPetIdOrderByOrderIndex(1L)).thenReturn(Collections.emptyList());
        PetDto.PetResponse resp = petService.getPetById(1L, null);
        assertEquals("Buddy", resp.getName());
        assertNull(resp.getOwnerPhone()); // no contact for anonymous
    }

    @Test @DisplayName("Get pet by ID: not found → exception")
    void getPetById_NotFound() {
        when(petRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> petService.getPetById(999L, null));
    }

    @Test @DisplayName("Delete pet: not owner → exception")
    void deletePet_NotOwner() {
        User other = User.builder().id(2L).email("other@test.com").build();
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));
        assertThrows(UnauthorizedException.class, () -> petService.deletePet(1L, "other@test.com"));
    }

    @Test @DisplayName("Get my pets: success")
    void getMyPets_Success() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testUser));
        when(petRepository.findByOwnerId(1L)).thenReturn(List.of(testPet));
        when(petPhotoRepository.findByPetIdOrderByOrderIndex(anyLong())).thenReturn(Collections.emptyList());
        List<PetDto.PetResponse> pets = petService.getMyPets("john@test.com");
        assertEquals(1, pets.size());
    }
}
