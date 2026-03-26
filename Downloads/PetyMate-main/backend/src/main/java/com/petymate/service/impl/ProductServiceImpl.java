package com.petymate.service.impl;

import com.petymate.config.RazorpayConfig;
import com.petymate.dto.*;
import com.petymate.entity.*;
import com.petymate.exception.*;
import com.petymate.repository.*;
import com.petymate.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    public PagedResponse<ProductDto.ProductResponse> getProducts(String category, String speciesTags, String brand,
            BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minRating, Boolean featuredOnly,
            String search, String sortBy, Pageable pageable) {

        Product.ProductCategory cat = category != null ? Product.ProductCategory.valueOf(category) : null;

        Page<Product> page = productRepository.findProductsFiltered(cat, speciesTags, brand,
                minPrice, maxPrice, minRating, featuredOnly, search, pageable);

        return PagedResponse.<ProductDto.ProductResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override
    public ProductDto.ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public void addReview(Long productId, String email, ProductDto.ReviewRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(user.getId(), Review.TargetType.PRODUCT, productId)) {
            throw new IllegalArgumentException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .reviewer(user).targetType(Review.TargetType.PRODUCT).targetId(productId)
                .rating(request.getRating()).comment(request.getComment()).build();
        reviewRepository.save(review);

        Double avgRating = reviewRepository.getAverageRating(Review.TargetType.PRODUCT, productId);
        long count = reviewRepository.countByTargetTypeAndTargetId(Review.TargetType.PRODUCT, productId);
        product.setRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        product.setReviewCount((int) count);
        productRepository.save(product);
    }

    private ProductDto.ProductResponse mapToResponse(Product p) {
        return ProductDto.ProductResponse.builder()
                .id(p.getId()).name(p.getName()).category(p.getCategory().name())
                .speciesTags(p.getSpeciesTags()).description(p.getDescription())
                .price(p.getPrice()).originalPrice(p.getOriginalPrice())
                .stockQty(p.getStockQty()).brand(p.getBrand()).photoUrl(p.getPhotoUrl())
                .rating(p.getRating()).reviewCount(p.getReviewCount()).isFeatured(p.getIsFeatured())
                .createdAt(p.getCreatedAt()).build();
    }
}
