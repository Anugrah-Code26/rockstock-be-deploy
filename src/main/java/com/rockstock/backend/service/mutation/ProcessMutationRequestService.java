package com.rockstock.backend.service.mutation;

import com.rockstock.backend.entity.stock.*;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.mutationJournal.dto.ProcessRequestDTO;
import com.rockstock.backend.infrastructure.mutationJournal.repository.MutationJournalRepository;
import com.rockstock.backend.infrastructure.util.security.AuthorizationUtil;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class ProcessMutationRequestService {
    private final WarehouseStockRepository warehouseStockRepository;
    private final MutationJournalRepository mutationJournalRepository;

    @Transactional
    public String processMutationRequest(Long journalId, Long warehouseId, boolean approved, ProcessRequestDTO processDTO) {
        MutationJournal destinationMutationJournal = mutationJournalRepository.findById(journalId)
                .orElseThrow(() -> new EntityNotFoundException("Stock journal not found"));

//        AuthorizationUtil.validateOriginAuthorization(destinationMutationJournal);

        if (!destinationMutationJournal.getOriginWarehouse().getId().equals(warehouseId)) {
            throw new AccessDeniedException("Access denied: The provided warehouse does not match the origin warehouse in this mutation request.");
        }

        if (destinationMutationJournal.getMutationStatus() != MutationStatus.PENDING) {
            throw new RuntimeException("The journal status is not pending");
        }

        if (destinationMutationJournal.getStockChangeType() != StockChangeType.TRANSFER) {
            throw new RuntimeException("The journal type is not transfer");
        }

        Warehouse originWarehouse = destinationMutationJournal.getOriginWarehouse();
        Warehouse destinationWarehouse = destinationMutationJournal.getDestinationWarehouse();

        WarehouseStock originStock = warehouseStockRepository.findByProductAndWarehouse(
                        destinationMutationJournal.getWarehouseStock().getProduct(), originWarehouse)
                .orElseThrow(() -> new RuntimeException("Stock not found in origin warehouse"));

        OffsetDateTime now = OffsetDateTime.now();
        long daysSinceRequest = ChronoUnit.DAYS.between(destinationMutationJournal.getCreatedAt(), now);

        if (daysSinceRequest >= 2) {
            destinationMutationJournal.setMutationStatus(MutationStatus.CANCELLED);
            destinationMutationJournal.setDescription("Request automatically cancelled after 2 days of no approval.");
            destinationMutationJournal.setUpdatedAt(now);
            mutationJournalRepository.save(destinationMutationJournal);
            return "Mutation request auto-cancelled due to no approval.";
        }

        String message;

        if (approved) {
            if ((originStock.getStockQuantity() - originStock.getLockedQuantity()) < destinationMutationJournal.getMutationQuantity()) {
                destinationMutationJournal.setMutationStatus(MutationStatus.CANCELLED);
                destinationMutationJournal.setDescription("Stock insufficient at approval time.");
                destinationMutationJournal.setUpdatedAt(now);
                mutationJournalRepository.save(destinationMutationJournal);
                return "Mutation request cancelled: Not enough stock.";
            }

            long previousStockQuantityOrigin = originStock.getStockQuantity();
            long newStockQuantityOrigin = previousStockQuantityOrigin - destinationMutationJournal.getMutationQuantity();

            originStock.setStockQuantity(newStockQuantityOrigin);
            warehouseStockRepository.save(originStock);

            MutationJournal originMutationJournal = new MutationJournal();
            originMutationJournal.setMutationQuantity(destinationMutationJournal.getMutationQuantity());
            originMutationJournal.setPreviousStockQuantity(previousStockQuantityOrigin);
            originMutationJournal.setNewStockQuantity(newStockQuantityOrigin);
            originMutationJournal.setWarehouseStock(originStock);
            originMutationJournal.setOriginWarehouse(originWarehouse);
            originMutationJournal.setDestinationWarehouse(destinationWarehouse);
            originMutationJournal.setStockChangeType(StockChangeType.TRANSFER);
            originMutationJournal.setStockAdjustmentType(StockAdjustmentType.NEGATIVE);
            originMutationJournal.setMutationStatus(MutationStatus.APPROVED);
            originMutationJournal.setRelatedJournal(destinationMutationJournal);
            originMutationJournal.setUpdatedAt(OffsetDateTime.now());
            String description = (processDTO.getDescription() != null && !processDTO.getDescription().trim().isEmpty())
                    ? processDTO.getDescription()
                    : "Stock transferred out from " + originWarehouse.getName() + " to " + destinationWarehouse.getName();

            originMutationJournal.setDescription(description);
            mutationJournalRepository.save(originMutationJournal);

            destinationMutationJournal.setMutationStatus(MutationStatus.APPROVED);
            destinationMutationJournal.setUpdatedAt(OffsetDateTime.now());
            destinationMutationJournal.getChildJournals().add(originMutationJournal);
            mutationJournalRepository.save(destinationMutationJournal);

            message = "Mutation request approved successfully. Stock deducted from origin warehouse and added to destination warehouse.";
        } else {
            destinationMutationJournal.setMutationStatus(MutationStatus.CANCELLED);
            String cancellationReason = (processDTO.getDescription() != null && !processDTO.getDescription().trim().isEmpty())
                    ? processDTO.getDescription()
                    : "Mutation request cancelled.";

            destinationMutationJournal.setDescription(cancellationReason);
            destinationMutationJournal.setUpdatedAt(OffsetDateTime.now());
            mutationJournalRepository.save(destinationMutationJournal);

            message = "Mutation request cancelled.";
        }
        return message;
    }


}