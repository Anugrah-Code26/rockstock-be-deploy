package com.rockstock.backend.service.mutation;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.stock.*;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.mutationJournal.dto.MutationRequestDTO;
import com.rockstock.backend.infrastructure.mutationJournal.repository.MutationJournalRepository;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.util.security.AuthorizationUtil;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateMutationRequestService {
    private final ProductRepository productRepository;
    private final MutationJournalRepository mutationJournalRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    @Transactional
    public void createMutationRequest(Long warehouseId, Long productId, MutationRequestDTO requestDTO) {
        AuthorizationUtil.validateDestinationAuthorization(warehouseId);

        Warehouse destinationWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new RuntimeException("Destination warehouse not found"));

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (warehouseId.equals(requestDTO.getOriginWarehouseId())) {
            throw new RuntimeException("Destination warehouse cannot be the same as origin warehouse");
        }

        Warehouse originWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(requestDTO.getOriginWarehouseId())
                .orElseThrow(() -> new RuntimeException("Origin warehouse not found"));

        WarehouseStock originStock = warehouseStockRepository.findByProductAndWarehouse(product, originWarehouse)
                .orElseThrow(() -> new RuntimeException("Stock not found in origin warehouse"));

        if ((originStock.getStockQuantity() - originStock.getLockedQuantity()) < requestDTO.getMutationQuantity()) {
            throw new RuntimeException("Not enough available stock in origin warehouse");
        }

        MutationJournal journal = new MutationJournal();
        journal.setMutationQuantity(requestDTO.getMutationQuantity());
        journal.setWarehouseStock(originStock);
        journal.setOriginWarehouse(originWarehouse);
        journal.setDestinationWarehouse(destinationWarehouse);
        journal.setStockChangeType(StockChangeType.TRANSFER);
        journal.setStockAdjustmentType(StockAdjustmentType.POSITIVE);
        journal.setMutationStatus(MutationStatus.PENDING);
        String description = (requestDTO.getDescription() != null && !requestDTO.getDescription().trim().isEmpty())
                ? requestDTO.getDescription()
                : "Stock transfer pending approval";
        journal.setDescription(description);
        journal.setUpdatedAt(OffsetDateTime.now());

        mutationJournalRepository.save(journal);
    }
}
