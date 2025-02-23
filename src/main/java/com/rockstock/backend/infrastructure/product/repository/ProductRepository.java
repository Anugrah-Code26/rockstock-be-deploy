package com.rockstock.backend.infrastructure.product.repository;

import com.rockstock.backend.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query("SELECT p FROM Product p WHERE p.id = :productId AND p.deletedAt IS NULL")
    Optional<Product> findByIdAndDeletedAtIsNull(@Param("productId") Long productId);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE LOWER(p.productName) = LOWER(:productName) AND p.deletedAt IS NULL")
    boolean existsByProductName(@Param("productName") String productName);

    @Query("SELECT p FROM Product p WHERE p.id = :productId AND p.deletedAt IS NOT NULL")
    Optional<Product> findByIdAndDeletedAtIsNotNull(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE Product p SET p.totalStock = :totalStock WHERE p.id = :productId")
    void updateTotalStock(@Param("productId") Long productId, @Param("totalStock") BigDecimal totalStock);
}