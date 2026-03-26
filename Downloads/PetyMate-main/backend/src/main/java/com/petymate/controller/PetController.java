package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<PetDto.PetResponse>> createPet(
            Authentication auth,
            @Valid @ModelAttribute PetDto.CreatePetRequest request,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos) {
        return ResponseEntity.ok(ApiResponse.success("Pet listed successfully",
                petService.createPet(auth.getName(), request, photos)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PetDto.PetResponse>>> getPets(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String listingType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) Integer radiusKm,
            @RequestParam(required = false) Boolean vaccinationStatus,
            @RequestParam(required = false) Boolean pedigreeOnly,
            @RequestParam(required = false) Boolean hasOwnSpace,
            @RequestParam(required = false) Integer ageMin,
            @RequestParam(required = false) Integer ageMax,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "NEWEST") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Authentication auth) {
        Sort sort = switch (sortBy) {
            case "AGE_ASC" -> Sort.by("ageMonths").ascending();
            case "AGE_DESC" -> Sort.by("ageMonths").descending();
            case "PRICE_ASC" -> Sort.by("price").ascending();
            case "PRICE_DESC" -> Sort.by("price").descending();
            default -> Sort.by("createdAt").descending();
        };
        Pageable pageable = PageRequest.of(page, size, sort);
        String email = auth != null ? auth.getName() : null;
        return ResponseEntity.ok(ApiResponse.success(petService.getPets(species, breed, gender, listingType,
                city, state, pincode, radiusKm, vaccinationStatus, pedigreeOnly, hasOwnSpace,
                ageMin, ageMax, minPrice, maxPrice, sortBy, pageable, email)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PetDto.PetResponse>> getPetById(
            @PathVariable Long id, Authentication auth) {
        String email = auth != null ? auth.getName() : null;
        return ResponseEntity.ok(ApiResponse.success(petService.getPetById(id, email)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PetDto.PetResponse>> updatePet(
            @PathVariable Long id, Authentication auth,
            @Valid @RequestBody PetDto.CreatePetRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Pet updated", petService.updatePet(id, auth.getName(), request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePet(@PathVariable Long id, Authentication auth) {
        petService.deletePet(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Pet removed", null));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PetDto.PetResponse>>> getMyPets(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(petService.getMyPets(auth.getName())));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id, Authentication auth, @RequestParam String status) {
        petService.updatePetStatus(id, auth.getName(), status);
        return ResponseEntity.ok(ApiResponse.success("Status updated", null));
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<ApiResponse<PetDto.PetPhotoDto>> addPhoto(
            @PathVariable Long id, Authentication auth, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("Photo added", petService.addPhoto(id, auth.getName(), file)));
    }

    @DeleteMapping("/{petId}/photos/{photoId}")
    public ResponseEntity<ApiResponse<Void>> deletePhoto(
            @PathVariable Long petId, @PathVariable Long photoId, Authentication auth) {
        petService.deletePhoto(petId, photoId, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Photo deleted", null));
    }
}
