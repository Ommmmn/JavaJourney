package com.petymate.service;

import com.petymate.dto.ProductDto;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.repository.*;
import com.petymate.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @InjectMocks private ProductServiceImpl productService;
    @Mock private ProductRepository productRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;

    @Test @DisplayName("Get product by ID: success")
    void getProductById_Success() {
        Product p = Product.builder().id(1L).name("Dog Food").category(Product.ProductCategory.FOOD)
                .price(BigDecimal.valueOf(599)).stockQty(50).rating(BigDecimal.valueOf(4.5)).reviewCount(10)
                .isFeatured(false).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        ProductDto.ProductResponse resp = productService.getProductById(1L);
        assertEquals("Dog Food", resp.getName());
        assertEquals(BigDecimal.valueOf(599), resp.getPrice());
    }

    @Test @DisplayName("Get product: not found")
    void getProductById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test @DisplayName("Add review: success")
    void addReview_Success() {
        User user = User.builder().id(1L).email("john@test.com").build();
        Product product = Product.builder().id(1L).name("Dog Food").build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, Review.TargetType.PRODUCT, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));
        when(reviewRepository.getAverageRating(Review.TargetType.PRODUCT, 1L)).thenReturn(4.5);
        when(reviewRepository.countByTargetTypeAndTargetId(Review.TargetType.PRODUCT, 1L)).thenReturn(1L);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertDoesNotThrow(() -> productService.addReview(1L, "john@test.com", new ProductDto.ReviewRequest(5, "Great!")));
        verify(reviewRepository).save(any(Review.class));
    }

    @Test @DisplayName("Add review: duplicate → exception")
    void addReview_Duplicate() {
        User user = User.builder().id(1L).email("john@test.com").build();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(Product.builder().id(1L).build()));
        when(reviewRepository.existsByReviewerIdAndTargetTypeAndTargetId(1L, Review.TargetType.PRODUCT, 1L)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> productService.addReview(1L, "john@test.com", new ProductDto.ReviewRequest(5, "Again")));
    }
}
