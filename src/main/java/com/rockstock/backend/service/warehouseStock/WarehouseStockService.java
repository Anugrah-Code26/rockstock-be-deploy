package com.rockstock.backend.service.warehouseStock;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.stock.WarehouseStock;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.warehouseStock.dto.WarehouseStockResponseDTO;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.infrastructure.warehouseStock.specification.FilterWarehouseStockSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseStockService {
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    @Transactional
    public WarehouseStockResponseDTO createWarehouseStock(Long productId, Long warehouseId) {
        // Check if product exists
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Check if warehouse exists
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        // Check if WarehouseStock already exists (excluding soft-deleted records)
        Optional<WarehouseStock> existingStock = warehouseStockRepository.findByProductAndWarehouse(product, warehouse);
        if (existingStock.isPresent()) {
            throw new IllegalStateException("WarehouseStock already exists for this product in the "+ warehouse.getName());
        }

        // Create new WarehouseStock with stockQuantity = 0
        WarehouseStock newStock = new WarehouseStock();
        newStock.setProduct(product);
        newStock.setWarehouse(warehouse);
        newStock.setStockQuantity(0L);

        WarehouseStock savedStock = warehouseStockRepository.save(newStock);

        return new WarehouseStockResponseDTO(
                savedStock.getId(),
                savedStock.getStockQuantity(),
                product.getProductName(),
                product.getId(),
                warehouse.getName(),
                warehouse.getId()
        );
    }

    @Transactional
    public void softDeleteWarehouseStock(Long stockId) {
        int updatedRows = warehouseStockRepository.softDelete(stockId, OffsetDateTime.now());

        if (updatedRows == 0) {
            throw new EntityNotFoundException("WarehouseStock not found or already deleted");
        }
    }
    
    @Transactional
    public void restoreWarehouseStock(Long stockId) {
        int updatedRows = warehouseStockRepository.restore(stockId);

        if (updatedRows == 0) {
            throw new EntityNotFoundException("WarehouseStock not found or not deleted.");
        }
    }

    public Page<WarehouseStockResponseDTO> getFilteredWarehouseStocks(String productName, String warehouseName, Pageable pageable) {
        Specification<WarehouseStock> spec = FilterWarehouseStockSpecification.withFilters(productName, warehouseName);

        Page<WarehouseStock> stocks = warehouseStockRepository.findAll(spec, pageable);

        if (stocks.isEmpty()) {
            throw new EntityNotFoundException("No warehouse stock records found.");
        }

        return stocks.map(stock -> new WarehouseStockResponseDTO(
                stock.getId(),
                stock.getStockQuantity(),
                stock.getProduct().getProductName(),
                stock.getProduct().getId(),
                stock.getWarehouse().getName(),
                stock.getWarehouse().getId()
        ));
    }

    public WarehouseStockResponseDTO getWarehouseStockById(Long stockId) {
        WarehouseStock stock = warehouseStockRepository.findByIdAndDeletedAtIsNull(stockId)
                .orElseThrow(() -> new EntityNotFoundException("WarehouseStock not found"));

        return new WarehouseStockResponseDTO(
                stock.getId(),
                stock.getStockQuantity(),
                stock.getProduct().getProductName(),
                stock.getProduct().getId(),
                stock.getWarehouse().getName(),
                stock.getWarehouse().getId()
        );
    }
}