package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductDto.ProductResponse>>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String speciesTags,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean featuredOnly,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NEWEST") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Sort sort = switch (sortBy) {
            case "PRICE_ASC" -> Sort.by("price").ascending();
            case "PRICE_DESC" -> Sort.by("price").descending();
            case "RATING" -> Sort.by("rating").descending();
            case "POPULAR" -> Sort.by("reviewCount").descending();
            default -> Sort.by("createdAt").descending();
        };
        return ResponseEntity.ok(ApiResponse.success(productService.getProducts(category, speciesTags, brand,
                minPrice, maxPrice, minRating, featuredOnly, search, sortBy, PageRequest.of(page, size, sort))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto.ProductResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ApiResponse<Void>> addReview(@PathVariable Long id, Authentication auth,
            @Valid @RequestBody ProductDto.ReviewRequest request) {
        productService.addReview(id, auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Review added", null));
    }
}
