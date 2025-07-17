package com.rockstock.backend.service.mutation;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.stock.*;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.mutationJournal.dto.StockAdjustmentRequestDTO;
import com.rockstock.backend.infrastructure.mutationJournal.repository.MutationJournalRepository;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.util.security.AuthorizationUtil;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AdjustStockService {
    private final WarehouseStockRepository warehouseStockRepository;
    private final MutationJournalRepository mutationJournalRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public void adjustStockRequest(Long warehouseId, Long productId, StockAdjustmentRequestDTO adjustmentRequestDTO) {
        Long newStockQuantity = adjustmentRequestDTO.getNewStockQuantity();
        if (newStockQuantity == null) {
            throw new IllegalArgumentException("New stock quantity must not be null");
        }

//        AuthorizationUtil.validateDestinationAuthorization(warehouseId);

        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String description = adjustmentRequestDTO.getDescription();
        if (description == null || description.trim().isEmpty()) {
            description = "Stock adjustment performed";
        }

        WarehouseStock stock = warehouseStockRepository.findByProductAndWarehouse(product, warehouse)
                .orElseGet(() -> {
                    WarehouseStock newStock = new WarehouseStock();
                    newStock.setProduct(product);
                    newStock.setWarehouse(warehouse);
                    newStock.setStockQuantity(0L);
                    newStock.setLockedQuantity(0L);
                    return warehouseStockRepository.save(newStock);
                });

        if (stock.getDeletedAt() != null) {
            throw new RuntimeException("Stock record has been deleted");
        }

        long previousStock = stock.getStockQuantity();
        long lockedQuantity = stock.getLockedQuantity();

        if (newStockQuantity < lockedQuantity) {
            throw new RuntimeException("Stock cannot be adjusted below locked quantity: " + lockedQuantity);
        }

        long quantityChange = Math.abs(newStockQuantity - previousStock);

        stock.setStockQuantity(newStockQuantity);
        warehouseStockRepository.save(stock);

        StockAdjustmentType stockAdjustmentType = (newStockQuantity > previousStock) ?
                StockAdjustmentType.POSITIVE :
                StockAdjustmentType.NEGATIVE;

        MutationJournal adjustmentJournal = new MutationJournal();
        adjustmentJournal.setWarehouseStock(stock);
        adjustmentJournal.setMutationQuantity(Math.abs(quantityChange));
        adjustmentJournal.setPreviousStockQuantity(previousStock);
        adjustmentJournal.setNewStockQuantity(newStockQuantity);
        adjustmentJournal.setStockChangeType(StockChangeType.STOCK_ADJUSTMENT);
        adjustmentJournal.setStockAdjustmentType(stockAdjustmentType);
        adjustmentJournal.setMutationStatus(MutationStatus.COMPLETED);
        adjustmentJournal.setDescription(description);
        adjustmentJournal.setDestinationWarehouse(warehouse);
        adjustmentJournal.setUpdatedAt(OffsetDateTime.now());


        mutationJournalRepository.save(adjustmentJournal);

        BigDecimal totalStock = warehouseStockRepository.getTotalStockByProductId(product.getId());
        product.setTotalStock(totalStock != null ? totalStock : BigDecimal.ZERO);
        productRepository.save(product);
    }
}
