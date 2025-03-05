package com.rockstock.backend.service.product;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductCategory;
import com.rockstock.backend.entity.product.ProductStatus;
import com.rockstock.backend.infrastructure.product.dto.CreateProductResponseDTO;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.productCategory.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CreateProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public CreateProductResponseDTO createDraftProduct() {
        Product product = new Product();
        product.setProductName("Draft Product");
        product.setDetail("This is a draft product.");
        product.setPrice(BigDecimal.ZERO);
        product.setWeight(BigDecimal.ZERO);
        product.setTotalStock(BigDecimal.ZERO);
        product.setStatus(ProductStatus.DRAFT);
        product.setCreatedAt(OffsetDateTime.now());
        product.setUpdatedAt(OffsetDateTime.now());

        ProductCategory defaultCategory = productCategoryRepository.findByCategoryId(1L)
                .orElseThrow(() -> new RuntimeException("Default category not found"));
        product.setProductCategory(defaultCategory);

        Product savedProduct = productRepository.save(product);

        return new CreateProductResponseDTO(savedProduct.getId(), savedProduct.getStatus());
    }
}