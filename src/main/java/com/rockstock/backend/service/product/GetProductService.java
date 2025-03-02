package com.rockstock.backend.service.product;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductStatus;
import com.rockstock.backend.infrastructure.product.dto.GetAllProductResponseDTO;
import com.rockstock.backend.infrastructure.product.dto.GetProductResponseDTO;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.product.specification.FilterProductSpecifications;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProductService {
    private final ProductRepository productRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    // Get Product by ID - excluding soft-deleted products
    public GetProductResponseDTO getProductById(Long productId) {
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ID cannot be null");
        }
        return productRepository.findByIdAndDeletedAtIsNull(productId)
                .map(GetProductResponseDTO::fromProduct)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Page<GetAllProductResponseDTO> getAllProducts(int page, int size, String name, String category, String sortField, String sortDirection) {
        if (StringUtils.isBlank(sortField)) {
            sortField = "updatedAt";
        }

        sortDirection = (StringUtils.equalsIgnoreCase(sortDirection, "DESC")) ? "DESC" : "ASC";

        List<String> allowedSortFields = Arrays.asList("updatedAt", "productName", "price", "category");
        if (!allowedSortFields.contains(sortField)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort field: " + sortField);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortField));
        Specification<Product> specification = Specification.where(FilterProductSpecifications.hasStatus(ProductStatus.ACTIVE))
                .and((root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt")));

        if (StringUtils.isNotBlank(name)) {
            specification = specification.and(FilterProductSpecifications.hasProductName(name));
        }

        if (StringUtils.isNotBlank(category)) {
            specification = specification.and(FilterProductSpecifications.hasCategoryName(category));
        }

        Page<Product> products = productRepository.findAll(specification, pageable);

        if (products.isEmpty()) {
            throw new RuntimeException("No products found with the specified criteria");
        }

        return products.map(product -> {
            BigDecimal totalStock = warehouseStockRepository.getTotalStockByProductId(product.getId());
            return GetAllProductResponseDTO.fromProduct(product, totalStock);
        });
    }
}

