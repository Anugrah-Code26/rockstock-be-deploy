package com.rockstock.backend.service.mutation;

import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.entity.stock.MutationStatus;
import com.rockstock.backend.entity.stock.StockChangeType;
import com.rockstock.backend.entity.stock.WarehouseStock;
import com.rockstock.backend.infrastructure.mutationJournal.dto.ConfirmStockRequestDTO;
import com.rockstock.backend.infrastructure.mutationJournal.repository.MutationJournalRepository;
import com.rockstock.backend.infrastructure.util.security.AuthorizationUtil;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmMutationStockService {

    private final WarehouseStockRepository warehouseStockRepository;
    private final MutationJournalRepository mutationJournalRepository;

    @Transactional
    public String confirmMutationStockService(Long journalId,
                                              Long warehouseId,
                                              boolean completed, ConfirmStockRequestDTO confirmDTO) {
        MutationJournal destinationMutationJournal = mutationJournalRepository.findById(journalId)
                .orElseThrow(() -> new RuntimeException("Stock journal not found"));

        MutationJournal originMutationJournal = mutationJournalRepository.findOriginJournalByDestinationId(journalId)
                .orElseThrow(() -> new RuntimeException("Related origin journal not found"));

//        AuthorizationUtil.validateDestinationAuthorization(warehouseId);

        if (!Objects.equals(destinationMutationJournal.getDestinationWarehouse().getId(), warehouseId)) {
            throw new RuntimeException("The warehouse is not the same");
        }

        if (destinationMutationJournal.getMutationStatus() != MutationStatus.APPROVED) {
            throw new RuntimeException("The journal status is not approved");
        }

        if (destinationMutationJournal.getStockChangeType() != StockChangeType.TRANSFER) {
            throw new RuntimeException("The journal type is not transfer");
        }

        long daysSinceApproval = ChronoUnit.DAYS.between(destinationMutationJournal.getUpdatedAt(), OffsetDateTime.now());

        if (daysSinceApproval >= 2) {
            WarehouseStock destinationStock = getOrCreateDestinationStock(destinationMutationJournal);
            destinationStock.setStockQuantity(destinationStock.getStockQuantity() + destinationMutationJournal.getMutationQuantity());
            warehouseStockRepository.save(destinationStock);

            destinationMutationJournal.setMutationStatus(MutationStatus.COMPLETED);
            destinationMutationJournal.setDescription("Stock mutation auto-completed after 2 days.");
            destinationMutationJournal.setUpdatedAt(OffsetDateTime.now());
            mutationJournalRepository.save(destinationMutationJournal);

            originMutationJournal.setMutationStatus(MutationStatus.COMPLETED);
            originMutationJournal.setDescription("Stock mutation completed");
            originMutationJournal.setUpdatedAt(OffsetDateTime.now());
            mutationJournalRepository.save(originMutationJournal);
            return destinationMutationJournal.getDescription();
        }

        String message;

        if (completed) {
            WarehouseStock destinationStock = getOrCreateDestinationStock(destinationMutationJournal);
            destinationMutationJournal.setPreviousStockQuantity(destinationStock.getStockQuantity());
            Long newStockQuantity = destinationStock.getStockQuantity() + destinationMutationJournal.getMutationQuantity();
            destinationMutationJournal.setNewStockQuantity(newStockQuantity);
            destinationStock.setStockQuantity(newStockQuantity);
            warehouseStockRepository.save(destinationStock);
            destinationMutationJournal.setMutationStatus(MutationStatus.COMPLETED);
            destinationMutationJournal.setDescription(
                    (confirmDTO.getDescription() != null && !confirmDTO.getDescription().trim().isEmpty())
                            ? confirmDTO.getDescription()
                            : "Stock confirm received, mutation request completed successfully");

            destinationMutationJournal.setUpdatedAt(OffsetDateTime.now());
            mutationJournalRepository.save(destinationMutationJournal);

            originMutationJournal.setMutationStatus(MutationStatus.COMPLETED);
            originMutationJournal.setDescription("Stock mutation completed");
            originMutationJournal.setUpdatedAt(OffsetDateTime.now());
            mutationJournalRepository.save(originMutationJournal);

            message = "Mutation completed successfully.";
        } else {
            WarehouseStock originStock = getOrCreateOriginStock(originMutationJournal);
            originStock.setStockQuantity(originStock.getStockQuantity() + destinationMutationJournal.getMutationQuantity());
            warehouseStockRepository.save(originStock);

            originMutationJournal.setMutationStatus(MutationStatus.FAILED);
            originMutationJournal.setDescription((confirmDTO.getDescription() != null && !confirmDTO.getDescription().trim().isEmpty())
                    ? confirmDTO.getDescription()
                    : "Stock confirmation not received, mutation request failed: reason not provided");

            originMutationJournal.setUpdatedAt(OffsetDateTime.now());
            mutationJournalRepository.save(originMutationJournal);

            destinationMutationJournal.setMutationStatus(MutationStatus.FAILED);
            destinationMutationJournal.setDescription("Stock confirmation not received");
            destinationMutationJournal.setUpdatedAt(OffsetDateTime.now());
            mutationJournalRepository.save(destinationMutationJournal);

            message = "Mutation failed.";
        }

        return message;
    }

    private WarehouseStock getOrCreateDestinationStock(MutationJournal destinationMutationJournal) {
        return warehouseStockRepository.findByProductAndWarehouse(
                destinationMutationJournal.getWarehouseStock().getProduct(),
                destinationMutationJournal.getDestinationWarehouse())
        .orElseGet(() -> {
            WarehouseStock newStock = new WarehouseStock();
            newStock.setProduct(destinationMutationJournal.getWarehouseStock().getProduct());
            newStock.setWarehouse(destinationMutationJournal.getDestinationWarehouse());
            newStock.setStockQuantity(0L);
            newStock.setLockedQuantity(0L);
            return warehouseStockRepository.save(newStock);
        });
    }

    private WarehouseStock getOrCreateOriginStock(MutationJournal originMutationJournal) {
        return warehouseStockRepository.findByProductAndWarehouse(
                originMutationJournal.getWarehouseStock().getProduct(),
                originMutationJournal.getOriginWarehouse())
        .orElseGet(() -> {
            WarehouseStock newStock = new WarehouseStock();
            newStock.setProduct(originMutationJournal.getWarehouseStock().getProduct());
            newStock.setWarehouse(originMutationJournal.getOriginWarehouse());
            newStock.setStockQuantity(0L);
            newStock.setLockedQuantity(0L);
            return warehouseStockRepository.save(newStock);
        });
    }
}