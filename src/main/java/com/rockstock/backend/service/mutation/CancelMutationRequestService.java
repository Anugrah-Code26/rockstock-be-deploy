package com.rockstock.backend.service.mutation;


import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.entity.stock.MutationStatus;
import com.rockstock.backend.infrastructure.mutationJournal.dto.CancelRequestDTO;
import com.rockstock.backend.infrastructure.mutationJournal.repository.MutationJournalRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelMutationRequestService {
    private final MutationJournalRepository mutationJournalRepository;

    @Transactional
    public String cancelPendingMutationRequest(Long journalId, Long warehouseId, CancelRequestDTO cancelRequestDTO) {
        MutationJournal destinationMutationJournal = mutationJournalRepository.findById(journalId)
                .orElseThrow(() -> new EntityNotFoundException("Stock journal not found"));

//        AuthorizationUtil.validateDestinationAuthorization(warehouseId);

        if (!destinationMutationJournal.getDestinationWarehouse().getId().equals(warehouseId)) {
            throw new AccessDeniedException("Access denied: Only the destination warehouse can cancel this request.");
        }

        if (destinationMutationJournal.getMutationStatus() != MutationStatus.PENDING) {
            throw new RuntimeException("The journal is not in pending status and cannot be cancelled.");
        }

        destinationMutationJournal.setMutationStatus(MutationStatus.CANCELLED);
        destinationMutationJournal.setUpdatedAt(OffsetDateTime.now());

        String reason = (cancelRequestDTO.getDescription() != null && !cancelRequestDTO.getDescription().trim().isEmpty())
                ? cancelRequestDTO.getDescription()
                : "Mutation request cancelled by destination warehouse.";
        destinationMutationJournal.setDescription(reason);

        mutationJournalRepository.save(destinationMutationJournal);

        return "Mutation request cancelled successfully.";
    }
}
