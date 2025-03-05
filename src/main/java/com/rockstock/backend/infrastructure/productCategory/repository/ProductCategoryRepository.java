package com.rockstock.backend.infrastructure.productCategory.repository;

import com.rockstock.backend.entity.product.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    @Query("SELECT COUNT(pc) > 0 FROM ProductCategory pc WHERE LOWER(pc.categoryName) = LOWER(:categoryName) AND pc.deletedAt IS NULL")
    boolean existsByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT pc FROM ProductCategory pc WHERE pc.Id = :categoryId AND pc.deletedAt IS NULL")
    Optional<ProductCategory> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT pc FROM ProductCategory pc WHERE pc.Id = :categoryId AND pc.deletedAt IS NOT NULL")
    Optional<ProductCategory> findDeletedCategoryById(@Param("categoryId") Long categoryId);

    @Query("""
        SELECT pc FROM ProductCategory pc
        WHERE pc.deletedAt IS NULL
        AND (:categoryName IS NULL OR :categoryName = '' OR LOWER(pc.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%')))
    """)
    Page<ProductCategory> findAllActiveCategories(@Param("categoryName") String categoryName, Pageable pageable);
}