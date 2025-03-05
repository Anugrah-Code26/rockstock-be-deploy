package com.rockstock.backend.service.product;

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
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
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

    public UpdateProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO updateProductRequestDTO) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getStatus() != ProductStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only draft products can be updated");
        }

        // Update only if the new name is different and doesn't already exist
        if (StringUtils.isNotBlank(updateProductRequestDTO.getProductName()) &&
                !updateProductRequestDTO.getProductName().equals(product.getProductName())) {
            boolean exists = productRepository.existsByProductName(updateProductRequestDTO.getProductName());
            if (exists) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Product name already exists");
            }
            product.setProductName(updateProductRequestDTO.getProductName());
        }

        // Update category only if a new category is provided and is different
        if (updateProductRequestDTO.getCategoryId() != null &&
                (product.getProductCategory() == null || !updateProductRequestDTO.getCategoryId().equals(product.getProductCategory().getId()))) {

            // Prevent assigning default category (ID = 1)
            if (updateProductRequestDTO.getCategoryId() == 1L) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot assign the default category.");
            }

            ProductCategory productCategory = productCategoryRepository.findByCategoryId(updateProductRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found or deleted: " + updateProductRequestDTO.getCategoryId()));

            product.setProductCategory(productCategory);
        }

        // Update fields only if they are different from current values
        if (StringUtils.isNotBlank(updateProductRequestDTO.getDetail()) &&
                !updateProductRequestDTO.getDetail().equals(product.getDetail())) {
            product.setDetail(updateProductRequestDTO.getDetail());
        }

        // Update price if changed
        if (updateProductRequestDTO.getPrice() != null &&
                (product.getPrice() == null || product.getPrice().compareTo(updateProductRequestDTO.getPrice()) != 0)) {
            product.setPrice(updateProductRequestDTO.getPrice());
        }

        // Update weight if changed
        if (updateProductRequestDTO.getWeight() != null &&
                (product.getWeight() == null || product.getWeight().compareTo(updateProductRequestDTO.getWeight()) != 0)) {
            product.setWeight(updateProductRequestDTO.getWeight());
        }

        // Validate and update status
        if (isValidProduct(product)) {
            product.setStatus(ProductStatus.ACTIVE);
                createWarehouseStockForProduct(product);
        }

        product.setUpdatedAt(OffsetDateTime.now());

        // Save the updated product
        Product updatedProduct = productRepository.save(product);
        return UpdateProductResponseDTO.fromProduct(updatedProduct);
    }

    private boolean isValidProduct(Product product) {
        boolean hasMainImage = productPictureRepository.existsByProductIdAndPosition(product.getId(), 1);

        return StringUtils.isNotBlank(product.getProductName()) && !product.getProductName().equals("Draft Product")
                && StringUtils.isNotBlank(product.getDetail()) && !product.getDetail().equals("This is a draft product.")
                && product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0
                && product.getWeight() != null && product.getWeight().compareTo(BigDecimal.ZERO) > 0
                && product.getProductCategory() != null && product.getProductCategory().getId() != 1L
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
                warehouseStocks.add(warehouseStock);
            }
        }

        if (!warehouseStocks.isEmpty()) {
            warehouseStockRepository.saveAll(warehouseStocks);
        }
    }
}