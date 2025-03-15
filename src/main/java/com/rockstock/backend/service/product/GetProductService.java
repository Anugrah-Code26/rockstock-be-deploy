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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProductService {
    private final ProductRepository productRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    public List<Product> getAllListProducts() {
        return productRepository.findAll();
    }

    public GetProductResponseDTO getProductById(Long productId) {
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ID cannot be null");
        }
        return productRepository.findByIdAndDeletedAtIsNull(productId)
                .map(product -> {
                    BigDecimal totalStock = warehouseStockRepository.getTotalStockByProductId(product.getId());
                    return GetProductResponseDTO.fromProduct(product, totalStock);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private List<Sort.Order> extractSortFields(String sortField, String sortDirection) {
        List<String> allowedSortFields = Arrays.asList("updatedAt", "productName", "price", "productCategory.id");

        // Split the input sortField by commas to handle multiple fields
        String[] fields = sortField.split(",");

        // Validate that all sort fields are allowed
        for (String field : fields) {
            if (!allowedSortFields.contains(field.trim())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort field: " + field);
            }
        }

        // Build Sort.Order list for multiple fields
        List<Sort.Order> orders = new ArrayList<>();
        for (String field : fields) {
            orders.add(new Sort.Order(Sort.Direction.fromString(sortDirection), field.trim()));
        }

        return orders;
    }

    public Page<GetAllProductResponseDTO> getAllProducts(int page, int size, String name, Long categoryId, String sortField, String sortDirection, ProductStatus[] statuses) {
        if (StringUtils.isBlank(sortField)) {
            sortField = "updatedAt";
        }

        sortDirection = (StringUtils.equalsIgnoreCase(sortDirection, "DESC")) ? "DESC" : "ASC";

        if (sortField.equalsIgnoreCase("name")) {
            sortField = "productName";
        }

        List<String> validSortFields = Arrays.asList("productName", "price", "updatedAt");

        if (!validSortFields.contains(sortField)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortField);
        }

        List<Sort.Order> orders = extractSortFields(sortField, sortDirection);
        Sort sort = Sort.by(orders);

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> specification = Specification.where(FilterProductSpecifications.hasStatus(statuses))
                .and((root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt")));

        if (StringUtils.isNotBlank(name)) {
            specification = specification.and(FilterProductSpecifications.hasProductName(name));
        }

        if (categoryId != null) {
            try {
                specification = specification.and(FilterProductSpecifications.hasCategoryId(categoryId));
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category ID format");
            }
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