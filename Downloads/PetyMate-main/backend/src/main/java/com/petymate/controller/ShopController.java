package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/shop/pets")
@RequiredArgsConstructor
public class ShopController {

    private final PetService petService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PetDto.PetResponse>>> getShopPets(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean pedigreeOnly,
            @RequestParam(required = false) Integer ageMin,
            @RequestParam(required = false) Integer ageMax,
            @RequestParam(required = false) String listingType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Authentication auth) {
        String lt = listingType != null ? listingType : null;
        String email = auth != null ? auth.getName() : null;
        return ResponseEntity.ok(ApiResponse.success(petService.getPets(species, breed, null, lt,
                city, state, null, null, null, pedigreeOnly, null,
                ageMin, ageMax, minPrice, maxPrice, "NEWEST",
                PageRequest.of(page, size, Sort.by("createdAt").descending()), email)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PetDto.PetResponse>> getShopPetDetail(
            @PathVariable Long id, Authentication auth) {
        String email = auth != null ? auth.getName() : null;
        return ResponseEntity.ok(ApiResponse.success(petService.getPetById(id, email)));
    }
}
