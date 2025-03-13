package com.rockstock.backend.infrastructure.productPicture.repository;

import com.rockstock.backend.entity.product.ProductPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPictureRepository extends JpaRepository<ProductPicture, Long> {
    @Query("SELECT COUNT(pp) FROM ProductPicture pp WHERE pp.product.id = :productId AND pp.deletedAt IS NULL")
    long countByProductId(@Param("productId") Long productId);

    @Query("SELECT CASE WHEN COUNT(pp) > 0 THEN TRUE ELSE FALSE END FROM ProductPicture pp WHERE pp.product.id = :productId AND pp.position = :position AND pp.deletedAt IS NULL")
    boolean existsByProductIdAndPosition(@Param("productId") Long productId, @Param("position") int position);

    @Query("SELECT pp FROM ProductPicture pp WHERE pp.id = :pictureId AND pp.product.id = :productId AND pp.deletedAt IS NULL ORDER BY pp.position ASC")
    Optional<ProductPicture> findByProductIdAndPictureId(@Param("productId") Long productId, @Param("pictureId") Long pictureId);

    @Query("SELECT pp FROM ProductPicture pp WHERE pp.product.id = :productId AND pp.position = :position AND pp.deletedAt IS NULL")
    Optional<ProductPicture> findByProductIdAndPosition(@Param("productId") Long productId, @Param("position") int position);

    @Query("SELECT pp FROM ProductPicture pp WHERE pp.product.id = :productId AND pp.deletedAt IS NULL ORDER BY pp.position ASC")
    List<ProductPicture> findAllByProductId(@Param("productId") Long productId);

    @Query("SELECT pp FROM ProductPicture pp WHERE pp.product.id = :productId AND pp.deletedAt IS NOT NULL ORDER BY pp.position ASC")
    List<ProductPicture> findByProductIdAndDeletedAtIsNotNull(Long productId);

    @Query("SELECT pp FROM ProductPicture pp WHERE pp.product.id = :productId AND pp.deletedAt IS NULL ORDER BY pp.position ASC")
    List<ProductPicture> findByProductIdAndDeletedAtIsNull(Long productId);
}