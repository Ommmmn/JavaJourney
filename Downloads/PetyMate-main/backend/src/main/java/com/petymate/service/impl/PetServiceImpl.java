package com.petymate.service.impl;

import com.petymate.dto.PagedResponse;
import com.petymate.dto.PetDto;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.exception.UnauthorizedException;
import com.petymate.repository.*;
import com.petymate.service.PetService;
import com.petymate.util.PincodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetPhotoRepository petPhotoRepository;
    private final UserRepository userRepository;
    private final MatchRequestRepository matchRequestRepository;

    @Value("${petymate.upload.dir}")
    private String uploadDir;

    @Value("${petymate.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public PetDto.PetResponse createPet(String email, PetDto.CreatePetRequest request, List<MultipartFile> photos) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BigDecimal[] latLng = PincodeUtil.getLatLng(request.getPincode());

        Pet pet = Pet.builder()
                .owner(owner)
                .name(request.getName())
                .species(Pet.Species.valueOf(request.getSpecies()))
                .breed(request.getBreed())
                .ageMonths(request.getAgeMonths())
                .gender(Pet.Gender.valueOf(request.getGender()))
                .color(request.getColor())
                .weightKg(request.getWeightKg())
                .vaccinationStatus(request.getVaccinationStatus() != null ? request.getVaccinationStatus() : false)
                .neutered(request.getNeutered() != null ? request.getNeutered() : false)
                .pedigreeCertified(request.getPedigreeCertified() != null ? request.getPedigreeCertified() : false)
                .hasOwnSpace(request.getHasOwnSpace() != null ? request.getHasOwnSpace() : false)
                .healthStatus(request.getHealthStatus())
                .bio(request.getBio())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .latitude(latLng != null ? latLng[0] : null)
                .longitude(latLng != null ? latLng[1] : null)
                .listingType(Pet.ListingType.valueOf(request.getListingType()))
                .price(request.getPrice())
                .status(Pet.PetStatus.ACTIVE)
                .build();

        pet = petRepository.save(pet);

        if (photos != null && !photos.isEmpty()) {
            for (int i = 0; i < photos.size(); i++) {
                String url = saveFile(photos.get(i), "pets", pet.getId());
                PetPhoto photo = PetPhoto.builder()
                        .pet(pet)
                        .photoUrl(url)
                        .isPrimary(i == 0)
                        .orderIndex(i)
                        .build();
                petPhotoRepository.save(photo);
            }
        }

        log.info("Pet created: {} by user {}", pet.getName(), email);
        return mapToResponse(pet, null, false);
    }

    @Override
    public PagedResponse<PetDto.PetResponse> getPets(String species, String breed, String gender,
            String listingType, String city, String state, String pincode, Integer radiusKm,
            Boolean vaccinationStatus, Boolean pedigreeOnly, Boolean hasOwnSpace,
            Integer ageMin, Integer ageMax, BigDecimal minPrice, BigDecimal maxPrice,
            String sortBy, Pageable pageable, String currentUserEmail) {

        Pet.Species speciesEnum = species != null ? Pet.Species.valueOf(species) : null;
        Pet.Gender genderEnum = gender != null ? Pet.Gender.valueOf(gender) : null;
        Pet.ListingType listingEnum = listingType != null ? Pet.ListingType.valueOf(listingType) : null;

        Page<Pet> page = petRepository.findPetsFiltered(speciesEnum, breed, genderEnum, listingEnum,
                city, state, vaccinationStatus, pedigreeOnly, hasOwnSpace, ageMin, ageMax, minPrice, maxPrice, pageable);

        BigDecimal[] userCoords = pincode != null ? PincodeUtil.getLatLng(pincode) : null;

        List<PetDto.PetResponse> content = page.getContent().stream()
                .map(pet -> {
                    Double distance = null;
                    if (userCoords != null && pet.getLatitude() != null && pet.getLongitude() != null) {
                        distance = PincodeUtil.haversineDistance(
                                userCoords[0].doubleValue(), userCoords[1].doubleValue(),
                                pet.getLatitude().doubleValue(), pet.getLongitude().doubleValue());
                    }
                    return mapToResponse(pet, distance, false);
                })
                .filter(r -> {
                    if (radiusKm != null && r.getDistanceKm() != null) {
                        return r.getDistanceKm() <= radiusKm;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        return PagedResponse.<PetDto.PetResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public PetDto.PetResponse getPetById(Long id, String currentUserEmail) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
        boolean showContact = false;
        if (pet.getListingType() == Pet.ListingType.SALE || pet.getListingType() == Pet.ListingType.ADOPTION) {
            showContact = true;
        } else if (currentUserEmail != null) {
            User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
            if (currentUser != null && pet.getOwner().getId().equals(currentUser.getId())) {
                showContact = true;
            }
        }
        return mapToResponse(pet, null, showContact);
    }

    @Override
    @Transactional
    public PetDto.PetResponse updatePet(Long id, String email, PetDto.CreatePetRequest request) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!pet.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only edit your own pets");
        }

        if (request.getName() != null) pet.setName(request.getName());
        if (request.getBreed() != null) pet.setBreed(request.getBreed());
        if (request.getAgeMonths() != null) pet.setAgeMonths(request.getAgeMonths());
        if (request.getColor() != null) pet.setColor(request.getColor());
        if (request.getWeightKg() != null) pet.setWeightKg(request.getWeightKg());
        if (request.getVaccinationStatus() != null) pet.setVaccinationStatus(request.getVaccinationStatus());
        if (request.getNeutered() != null) pet.setNeutered(request.getNeutered());
        if (request.getPedigreeCertified() != null) pet.setPedigreeCertified(request.getPedigreeCertified());
        if (request.getHasOwnSpace() != null) pet.setHasOwnSpace(request.getHasOwnSpace());
        if (request.getHealthStatus() != null) pet.setHealthStatus(request.getHealthStatus());
        if (request.getBio() != null) pet.setBio(request.getBio());
        if (request.getCity() != null) pet.setCity(request.getCity());
        if (request.getState() != null) pet.setState(request.getState());
        if (request.getPincode() != null) {
            pet.setPincode(request.getPincode());
            BigDecimal[] coords = PincodeUtil.getLatLng(request.getPincode());
            if (coords != null) { pet.setLatitude(coords[0]); pet.setLongitude(coords[1]); }
        }
        if (request.getPrice() != null) pet.setPrice(request.getPrice());

        petRepository.save(pet);
        return mapToResponse(pet, null, true);
    }

    @Override
    @Transactional
    public void deletePet(Long id, String email) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!pet.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own pets");
        }
        pet.setStatus(Pet.PetStatus.INACTIVE);
        petRepository.save(pet);
    }

    @Override
    public List<PetDto.PetResponse> getMyPets(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return petRepository.findByOwnerId(user.getId()).stream()
                .map(pet -> mapToResponse(pet, null, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updatePetStatus(Long id, String email, String status) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!pet.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only update your own pets");
        }
        pet.setStatus(Pet.PetStatus.valueOf(status));
        petRepository.save(pet);
    }

    @Override
    @Transactional
    public PetDto.PetPhotoDto addPhoto(Long petId, String email, MultipartFile file) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!pet.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only add photos to your own pets");
        }
        String url = saveFile(file, "pets", petId);
        int nextIndex = petPhotoRepository.findByPetIdOrderByOrderIndex(petId).size();
        PetPhoto photo = PetPhoto.builder()
                .pet(pet).photoUrl(url).isPrimary(false).orderIndex(nextIndex).build();
        photo = petPhotoRepository.save(photo);
        return PetDto.PetPhotoDto.builder()
                .id(photo.getId()).photoUrl(photo.getPhotoUrl())
                .isPrimary(photo.getIsPrimary()).orderIndex(photo.getOrderIndex()).build();
    }

    @Override
    @Transactional
    public void deletePhoto(Long petId, Long photoId, String email) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!pet.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own pet photos");
        }
        petPhotoRepository.deleteById(photoId);
    }

    private PetDto.PetResponse mapToResponse(Pet pet, Double distance, boolean showContact) {
        List<PetDto.PetPhotoDto> photos = petPhotoRepository.findByPetIdOrderByOrderIndex(pet.getId())
                .stream()
                .map(p -> PetDto.PetPhotoDto.builder()
                        .id(p.getId()).photoUrl(p.getPhotoUrl())
                        .isPrimary(p.getIsPrimary()).orderIndex(p.getOrderIndex()).build())
                .collect(Collectors.toList());

        return PetDto.PetResponse.builder()
                .id(pet.getId())
                .ownerId(pet.getOwner().getId())
                .ownerName(pet.getOwner().getName())
                .name(pet.getName())
                .species(pet.getSpecies() != null ? pet.getSpecies().name() : null)
                .breed(pet.getBreed())
                .ageMonths(pet.getAgeMonths())
                .gender(pet.getGender() != null ? pet.getGender().name() : null)
                .color(pet.getColor())
                .weightKg(pet.getWeightKg())
                .vaccinationStatus(pet.getVaccinationStatus())
                .neutered(pet.getNeutered())
                .pedigreeCertified(pet.getPedigreeCertified())
                .hasOwnSpace(pet.getHasOwnSpace())
                .healthStatus(pet.getHealthStatus())
                .bio(pet.getBio())
                .city(pet.getCity())
                .state(pet.getState())
                .pincode(pet.getPincode())
                .latitude(pet.getLatitude())
                .longitude(pet.getLongitude())
                .listingType(pet.getListingType() != null ? pet.getListingType().name() : null)
                .price(pet.getPrice())
                .status(pet.getStatus() != null ? pet.getStatus().name() : null)
                .photos(photos)
                .distanceKm(distance != null ? Math.round(distance * 10.0) / 10.0 : null)
                .ownerPhone(showContact ? pet.getOwner().getPhone() : null)
                .ownerEmail(showContact ? pet.getOwner().getEmail() : null)
                .createdAt(pet.getCreatedAt())
                .build();
    }

    private String saveFile(MultipartFile file, String subDir, Long id) {
        try {
            String filename = subDir + "_" + id + "_" + System.currentTimeMillis() +
                    getExtension(file.getOriginalFilename());
            Path uploadPath = Paths.get(uploadDir, subDir);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());
            return baseUrl + "/files/" + subDir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    private String getExtension(String filename) {
        if (filename != null && filename.contains(".")) return filename.substring(filename.lastIndexOf("."));
        return ".jpg";
    }
}
