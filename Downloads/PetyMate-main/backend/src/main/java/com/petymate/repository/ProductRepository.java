package com.petymate.repository;

import com.petymate.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:speciesTags IS NULL OR p.speciesTags LIKE CONCAT('%', :speciesTags, '%')) AND " +
           "(:brand IS NULL OR LOWER(p.brand) = LOWER(:brand)) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minRating IS NULL OR p.rating >= :minRating) AND " +
           "(:featuredOnly IS NULL OR p.isFeatured = :featuredOnly) AND " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findProductsFiltered(
            @Param("category") Product.ProductCategory category,
            @Param("speciesTags") String speciesTags,
            @Param("brand") String brand,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("minRating") java.math.BigDecimal minRating,
            @Param("featuredOnly") Boolean featuredOnly,
            @Param("search") String search,
            Pageable pageable);
}
