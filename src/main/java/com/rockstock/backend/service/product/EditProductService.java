package com.rockstock.backend.service.product;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductCategory;
import com.rockstock.backend.entity.product.ProductStatus;
import com.rockstock.backend.infrastructure.product.dto.EditProductRequestDTO;
import com.rockstock.backend.infrastructure.product.dto.EditProductResponseDTO;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.productCategory.repository.ProductCategoryRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
@Transactional
@AllArgsConstructor
public class EditProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public EditProductResponseDTO editProduct(Long id, EditProductRequestDTO editProductRequestDTO) {
        String role = Claims.getRoleFromJwt();
        if (!"Super Admin".equalsIgnoreCase(role)) {
            throw new AuthorizationDeniedException("Access denied: Only Super Admin can perform this action.");
        }
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only active products can be updated");
        }

        if (StringUtils.isNotBlank(editProductRequestDTO.getProductName()) &&
                !editProductRequestDTO.getProductName().equals(product.getProductName())) {
            boolean exists = productRepository.existsByProductName(editProductRequestDTO.getProductName());
            if (exists) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Product name already exists");
            }
            product.setProductName(editProductRequestDTO.getProductName());
        }

        if (editProductRequestDTO.getCategoryId() != null &&
                (product.getProductCategory() == null || !editProductRequestDTO.getCategoryId().equals(product.getProductCategory().getId()))) {

            if (editProductRequestDTO.getCategoryId() == 1L) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot assign the default category.");
            }

            ProductCategory productCategory = productCategoryRepository.findByCategoryId(editProductRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found or deleted: " + editProductRequestDTO.getCategoryId()));

            product.setProductCategory(productCategory);
        }

        if (StringUtils.isNotBlank(editProductRequestDTO.getDetail()) &&
                !editProductRequestDTO.getDetail().equals(product.getDetail())) {
            product.setDetail(editProductRequestDTO.getDetail());
        }

        if (editProductRequestDTO.getPrice() != null &&
                (product.getPrice() == null || product.getPrice().compareTo(editProductRequestDTO.getPrice()) != 0)) {
            product.setPrice(editProductRequestDTO.getPrice());
        }

        if (editProductRequestDTO.getWeight() != null &&
                (product.getWeight() == null || product.getWeight().compareTo(editProductRequestDTO.getWeight()) != 0)) {
            product.setWeight(editProductRequestDTO.getWeight());
        }

        product.setUpdatedAt(OffsetDateTime.now());

        Product editedProduct = productRepository.save(product);
        return EditProductResponseDTO.fromProduct(editedProduct);
    }
}
