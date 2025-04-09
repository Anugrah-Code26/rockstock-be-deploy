package com.rockstock.backend.service.product;

import com.rockstock.backend.common.exceptions.DuplicateDataException;
import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductCategory;
import com.rockstock.backend.entity.product.ProductStatus;
import com.rockstock.backend.entity.stock.WarehouseStock;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.product.dto.UpdateProductRequestDTO;
import com.rockstock.backend.infrastructure.product.dto.UpdateProductResponseDTO;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.productCategory.repository.ProductCategoryRepository;
import com.rockstock.backend.infrastructure.productPicture.repository.ProductPictureRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UpdateProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductPictureRepository productPictureRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    public UpdateProductResponseDTO updateProductToActive(Long id, UpdateProductRequestDTO updateProductRequestDTO) {
        String role = Claims.getRoleFromJwt();
        if (!"Super Admin".equalsIgnoreCase(role)) {
            throw new AuthorizationDeniedException("Access denied: Only Super Admin can perform this action.");
        }
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getStatus() != ProductStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only draft products can be updated");
        }

        if (StringUtils.isNotBlank(updateProductRequestDTO.getProductName()) &&
                !updateProductRequestDTO.getProductName().equals(product.getProductName())) {
            boolean exists = productRepository.existsByProductName(updateProductRequestDTO.getProductName());
            if (exists) {
                throw new DuplicateDataException("Product name already exists");
            }
            product.setProductName(updateProductRequestDTO.getProductName());
        }

        if (updateProductRequestDTO.getCategoryId() != null &&
                (product.getProductCategory() == null || !updateProductRequestDTO.getCategoryId().equals(product.getProductCategory().getId()))) {

            ProductCategory productCategory = productCategoryRepository.findByCategoryId(updateProductRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found or deleted: " + updateProductRequestDTO.getCategoryId()));

            product.setProductCategory(productCategory);
        }

        if (StringUtils.isNotBlank(updateProductRequestDTO.getDetail()) &&
                !updateProductRequestDTO.getDetail().equals(product.getDetail())) {
            product.setDetail(updateProductRequestDTO.getDetail());
        }

        if (updateProductRequestDTO.getPrice() != null &&
                (product.getPrice() == null || product.getPrice().compareTo(updateProductRequestDTO.getPrice()) != 0)) {
            product.setPrice(updateProductRequestDTO.getPrice());
        }

        if (updateProductRequestDTO.getWeight() != null &&
                (product.getWeight() == null || product.getWeight().compareTo(updateProductRequestDTO.getWeight()) != 0)) {
            product.setWeight(updateProductRequestDTO.getWeight());
        }

        if (!isValidProduct(product)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not valid. Please ensure all fields are correctly filled.");
        } else {
            product.setStatus(ProductStatus.ACTIVE);
            createWarehouseStockForProduct(product);
        }

        product.setUpdatedAt(OffsetDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return UpdateProductResponseDTO.fromProduct(updatedProduct);
    }

    private boolean isValidProduct(Product product) {
        boolean hasMainImage = productPictureRepository.existsByProductIdAndPosition(product.getId(), 1);

        return StringUtils.isNotBlank(product.getProductName()) && !product.getProductName().equals("Draft Product")
                && StringUtils.isNotBlank(product.getDetail()) && !product.getDetail().equals("This is a draft product.")
                && product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0
                && product.getWeight() != null && product.getWeight().compareTo(BigDecimal.ZERO) > 0
                && product.getProductCategory() != null
                && hasMainImage;
    }

    private void createWarehouseStockForProduct(Product product) {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<WarehouseStock> warehouseStocks = new ArrayList<>();

        for (Warehouse warehouse : warehouses) {
            if (!warehouseStockRepository.existsByProductAndWarehouse(product.getId(), warehouse.getId())) {
                WarehouseStock warehouseStock = new WarehouseStock();
                warehouseStock.setProduct(product);
                warehouseStock.setWarehouse(warehouse);
                warehouseStock.setStockQuantity(0L);
                warehouseStock.setLockedQuantity(0L);
                warehouseStocks.add(warehouseStock);
            }
        }

        if (!warehouseStocks.isEmpty()) {
            warehouseStockRepository.saveAll(warehouseStocks);
        }
    }

    public UpdateProductResponseDTO updateProductToDraft(Long id, UpdateProductRequestDTO updateProductRequestDTO) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getStatus() != ProductStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only draft products can be updated");
        }

        if (StringUtils.isNotBlank(updateProductRequestDTO.getProductName())) {
            product.setProductName(updateProductRequestDTO.getProductName());
        }

        if (updateProductRequestDTO.getCategoryId() != null &&
                (product.getProductCategory() == null || !updateProductRequestDTO.getCategoryId().equals(product.getProductCategory().getId()))) {

            ProductCategory productCategory = productCategoryRepository.findByCategoryId(updateProductRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found or deleted: " + updateProductRequestDTO.getCategoryId()));

            product.setProductCategory(productCategory);
        }

        if (StringUtils.isNotBlank(updateProductRequestDTO.getDetail()) &&
                !updateProductRequestDTO.getDetail().equals(product.getDetail())) {
            product.setDetail(updateProductRequestDTO.getDetail());
        }

        if (updateProductRequestDTO.getPrice() != null &&
                (product.getPrice() == null || product.getPrice().compareTo(updateProductRequestDTO.getPrice()) != 0)) {
            product.setPrice(updateProductRequestDTO.getPrice());
        }

        if (updateProductRequestDTO.getWeight() != null &&
                (product.getWeight() == null || product.getWeight().compareTo(updateProductRequestDTO.getWeight()) != 0)) {
            product.setWeight(updateProductRequestDTO.getWeight());
        }

        product.setUpdatedAt(OffsetDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return UpdateProductResponseDTO.fromProduct(updatedProduct);
    }

}