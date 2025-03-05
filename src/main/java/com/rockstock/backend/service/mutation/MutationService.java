package com.rockstock.backend.service.mutation;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.entity.stock.MutationStatus;
import com.rockstock.backend.entity.stock.StockChangeType;
import com.rockstock.backend.entity.stock.WarehouseStock;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.mutationJournal.dto.*;
import com.rockstock.backend.infrastructure.mutationJournal.specification.FilterMutationJournalSpecification;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import com.rockstock.backend.infrastructure.mutationJournal.repository.MutationJournalRepository;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class MutationService {

    private final WarehouseStockRepository warehouseStockRepository;
    private final MutationJournalRepository mutationJournalRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public void mutationRequestService(MutationRequestDTO requestDTO) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Warehouse originWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(requestDTO.getOriginWarehouseId())
                .orElseThrow(() -> new RuntimeException("Origin warehouse not found"));

        Warehouse destinationWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(requestDTO.getDestinationWarehouseId())
                .orElseThrow(() -> new RuntimeException("Destination warehouse not found"));

        WarehouseStock originStock = warehouseStockRepository.findByProductAndWarehouse(product, originWarehouse)
                .orElseThrow(() -> new RuntimeException("Stock not found in origin warehouse"));

        if (originStock.getStockQuantity() < requestDTO.getStockQuantity()) {
            throw new RuntimeException("Not enough stock in origin warehouse");
        }

        MutationJournal journal = new MutationJournal();
        journal.setWarehouseStock(originStock);
        journal.setMutationQuantity(requestDTO.getStockQuantity());
        journal.setPreviousStockQuantity(originStock.getStockQuantity());
        journal.setNewStockQuantity(originStock.getStockQuantity() - requestDTO.getStockQuantity()); // Only update origin here
        journal.setOriginWarehouse(originWarehouse);
        journal.setDestinationWarehouse(destinationWarehouse);
        journal.setStockChangeType(StockChangeType.TRANSFER);
        journal.setMutationStatus(MutationStatus.PENDING);

        mutationJournalRepository.save(journal);
    }

    @Transactional
    public String processMutationRequestService(Long journalId, boolean approved, ProcessRequestDTO processDTO) {
        MutationJournal journal = mutationJournalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Stock journal not found"));

        WarehouseStock originStock = warehouseStockRepository.findByProductAndWarehouse(
                        journal.getWarehouseStock().getProduct(), journal.getOriginWarehouse())
                .orElseThrow(() -> new RuntimeException("Stock not found in origin warehouse"));

        if (journal.getMutationStatus() != MutationStatus.PENDING) {
            throw new RuntimeException("The journal status is not pending");
        }

        // Calculate time difference from createdAt
        OffsetDateTime now = OffsetDateTime.now();
        long daysSinceRequest = ChronoUnit.DAYS.between(journal.getCreatedAt(), now);

        if (daysSinceRequest >= 3) {
            journal.setMutationStatus(MutationStatus.CANCELLED);
            journal.setDescription("Request automatically cancelled after 3 days of no approval.");
            mutationJournalRepository.save(journal);
            return "Mutation request auto-cancelled due to no approval.";
        }

        String message;

        if (approved) {
            if (originStock.getStockQuantity() < journal.getMutationQuantity()) {
                journal.setMutationStatus(MutationStatus.CANCELLED);
                journal.setDescription("Stock insufficient at approval time.");
                message = "Mutation request cancelled: Not enough stock.";
            } else {
                originStock.setStockQuantity(originStock.getStockQuantity() - journal.getMutationQuantity());
                warehouseStockRepository.save(originStock);

                journal.setMutationStatus(MutationStatus.APPROVED);
                journal.setDescription((processDTO.getDescription() != null && !processDTO.getDescription().trim().isEmpty())
                        ? processDTO.getDescription()
                        : "Stock mutation approved.");
                message = "Mutation request approved successfully.";
            }
        } else {
            journal.setMutationStatus(MutationStatus.CANCELLED);
            journal.setDescription((processDTO.getDescription() != null && !processDTO.getDescription().trim().isEmpty())
                    ? processDTO.getDescription()
                    : "Mutation request cancelled.");
            message = "Mutation request cancelled: " + journal.getDescription();
        }

        mutationJournalRepository.save(journal);
        return message;
    }

    @Transactional
    public String confirmReceivedStockService(Long journalId, boolean completed, ConfirmStockRequestDTO confirmDTO) {
        MutationJournal journal = mutationJournalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Stock journal not found"));

        if (journal.getMutationStatus() != MutationStatus.APPROVED) {
            throw new RuntimeException("The journal status is not approved");
        }

        WarehouseStock destinationStock = warehouseStockRepository.findByProductAndWarehouse(
                        journal.getWarehouseStock().getProduct(), journal.getDestinationWarehouse())
                .orElseGet(() -> {
                    WarehouseStock newStock = new WarehouseStock();
                    newStock.setProduct(journal.getWarehouseStock().getProduct());
                    newStock.setWarehouse(journal.getDestinationWarehouse());
                    newStock.setStockQuantity(0L);
                    return warehouseStockRepository.save(newStock);
                });

        OffsetDateTime now = OffsetDateTime.now();
        long daysSinceApproval = ChronoUnit.DAYS.between(journal.getUpdatedAt(), now);

        if (daysSinceApproval >= 3) {
            // Auto-complete and update stock
            destinationStock.setStockQuantity(destinationStock.getStockQuantity() + journal.getMutationQuantity());
            warehouseStockRepository.save(destinationStock);

            journal.setMutationStatus(MutationStatus.COMPLETED);
            journal.setDescription("Stock mutation confirm request auto-completed after 3 days.");
            mutationJournalRepository.save(journal);
            return "Stock mutation confirm request auto-completed after 3 days.";
        }

        String message;

        if (completed) {
            destinationStock.setStockQuantity(destinationStock.getStockQuantity() + journal.getMutationQuantity());
            warehouseStockRepository.save(destinationStock);

            journal.setMutationStatus(MutationStatus.COMPLETED);
            journal.setDescription((confirmDTO.getDescription() != null && !confirmDTO.getDescription().trim().isEmpty())
                    ? confirmDTO.getDescription()
                    : "Stock confirm received, mutation request completed successfully");
            message = "Mutation completed successfully.";
        } else {
            WarehouseStock originStock = warehouseStockRepository.findByProductAndWarehouse(
                            journal.getWarehouseStock().getProduct(), journal.getOriginWarehouse())
                    .orElseThrow(() -> new RuntimeException("Stock not found in origin warehouse"));

            originStock.setStockQuantity(originStock.getStockQuantity() + journal.getMutationQuantity());
            warehouseStockRepository.save(originStock);

            journal.setMutationStatus(MutationStatus.FAILED);
            journal.setDescription((confirmDTO.getDescription() != null && !confirmDTO.getDescription().trim().isEmpty())
                    ? confirmDTO.getDescription()
                    : "Stock confirmation not received, mutation request failed: reason not provided");
            message = "Mutation failed.";
        }

        mutationJournalRepository.save(journal);
        return message;
    }

    @Transactional
    public void adjustStock(Long warehouseId, Long productId, StockAdjustmentRequestDTO adjustmentRequestDTO) {
        Long newStockQuantity = adjustmentRequestDTO.getNewStockQuantity();
        String description = adjustmentRequestDTO.getDescription();
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        WarehouseStock stock = warehouseStockRepository.findByProductAndWarehouse(product, warehouse)
                .orElseGet(() -> {
                    WarehouseStock newStock = new WarehouseStock();
                    newStock.setProduct(product);
                    newStock.setWarehouse(warehouse);
                    newStock.setStockQuantity(0L);
                    return warehouseStockRepository.save(newStock);
                });

        if (stock.getDeletedAt() != null) {
            throw new RuntimeException("Stock record has been deleted");
        }

        long previousStock = stock.getStockQuantity();
        long quantityChange = newStockQuantity - previousStock;// Store previous stock before updating

        if (newStockQuantity < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }

        stock.setStockQuantity(newStockQuantity);
        warehouseStockRepository.save(stock);

        // Determine if it's a positive or negative stock adjustment
        StockChangeType stockChangeType = (quantityChange > 0) ?
                StockChangeType.STOCK_ADJUSTMENT_POSITIVE :
                StockChangeType.STOCK_ADJUSTMENT_NEGATIVE;

        // Create stock adjustment journal
        MutationJournal adjustmentJournal = new MutationJournal();
        adjustmentJournal.setWarehouseStock(stock);
        adjustmentJournal.setMutationQuantity(Math.abs(quantityChange));
        adjustmentJournal.setPreviousStockQuantity(previousStock);
        adjustmentJournal.setNewStockQuantity(newStockQuantity);
        adjustmentJournal.setStockChangeType(stockChangeType);
        adjustmentJournal.setMutationStatus(MutationStatus.COMPLETED);
        adjustmentJournal.setDescription(description);
        adjustmentJournal.setDestinationWarehouse(warehouse);

        mutationJournalRepository.save(adjustmentJournal);

        BigDecimal totalStock = BigDecimal.valueOf(warehouseStockRepository.sumStockByProduct(product));
        product.setTotalStock(totalStock);
        productRepository.save(product);
    }

    @Transactional
    public Page<GetMutationResponseDTO> getAllMutationJournals(Long productId, String warehouseName, MutationStatus status, String sortByUpdatedAt, Pageable pageable) {
        Specification<MutationJournal> spec = FilterMutationJournalSpecification.withFilters(productId, warehouseName, status);

        // Sort by productName (A-Z) and then updatedAt (asc/desc)
        Sort sort = Sort.by(Sort.Order.asc("warehouseStock.product.productName"))
                .and(sortByUpdatedAt.equalsIgnoreCase("desc") ? Sort.by(Sort.Order.desc("updatedAt"))
                        : Sort.by(Sort.Order.asc("updatedAt")));

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return mutationJournalRepository.findAll(spec, sortedPageable)
                .map(journal -> new GetMutationResponseDTO(
                        journal.getId(),
                        journal.getWarehouseStock().getProduct().getId(),
                        journal.getWarehouseStock().getProduct().getProductName(),
                        journal.getOriginWarehouse() != null ? journal.getOriginWarehouse().getName() : "N/A",
                        journal.getDestinationWarehouse() != null ? journal.getDestinationWarehouse().getName() : "N/A",
                        journal.getMutationQuantity(),
                        journal.getPreviousStockQuantity(),
                        journal.getNewStockQuantity(),
                        journal.getStockChangeType(),
                        journal.getMutationStatus(),
                        journal.getDescription(),
                        journal.getUpdatedAt()
                ));
    }

    public GetMutationResponseDTO getMutationJournalById(Long journalId) {
        return mutationJournalRepository.findById(journalId)
                .map(journal -> new GetMutationResponseDTO(
                        journal.getId(),
                        journal.getWarehouseStock().getProduct().getId(),
                        journal.getWarehouseStock().getProduct().getProductName(),
                        journal.getOriginWarehouse() != null ? journal.getOriginWarehouse().getName() : "N/A",
                        journal.getDestinationWarehouse() != null ? journal.getDestinationWarehouse().getName() : "N/A",
                        journal.getMutationQuantity(),
                        journal.getPreviousStockQuantity(),
                        journal.getNewStockQuantity(),
                        journal.getStockChangeType(),
                        journal.getMutationStatus(),
                        journal.getDescription(),
                        journal.getUpdatedAt()
                ))
                .orElseThrow(() -> new RuntimeException("Mutation Journal not found"));
    }
}