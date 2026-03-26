package com.petymate.service;

import com.petymate.dto.OrderDto;
import com.petymate.dto.PagedResponse;
import com.petymate.dto.ProductDto;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface ProductService {
    PagedResponse<ProductDto.ProductResponse> getProducts(String category, String speciesTags, String brand,
            BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minRating, Boolean featuredOnly,
            String search, String sortBy, Pageable pageable);
    ProductDto.ProductResponse getProductById(Long id);
    void addReview(Long productId, String email, ProductDto.ReviewRequest request);
}

