package com.rockstock.backend.service.mutation;

import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.infrastructure.mutationJournal.dto.GetAllMutationResponseDTO;
import com.rockstock.backend.infrastructure.mutationJournal.dto.GetMutationResponseDTO;
import com.rockstock.backend.infrastructure.mutationJournal.repository.MutationJournalRepository;
import com.rockstock.backend.infrastructure.mutationJournal.specification.FilterMutationJournalSpecification;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GetMutationJournalService {
    private final MutationJournalRepository mutationJournalRepository;

    @Transactional
    public Page<GetAllMutationResponseDTO> getAllMutationJournals(
            String productName, Long warehouseId, String status,
            String adjustmentType, String stockChangeType,
            String sortDirection, Pageable pageable) {

//        String currentUserRole = Claims.getRoleFromJwt();
//        // Allow only Super Admin and Warehouse Admin.
//        if (!"Super Admin".equalsIgnoreCase(currentUserRole) &&
//                !"Warehouse Admin".equalsIgnoreCase(currentUserRole)) {
//            throw new AccessDeniedException("Access Denied: Only Super Admin or Warehouse Admin can access this data.");
//        }
//
//        // If the user is a Warehouse Admin, ensure they only see their allowed warehouse.
//        if ("Warehouse Admin".equalsIgnoreCase(currentUserRole)) {
//            List<Long> allowedWarehouseIds = Claims.getWarehouseIdsFromJwt();
//            if (warehouseId == null || !allowedWarehouseIds.contains(warehouseId)) {
//                throw new AccessDeniedException("Access Denied: You do not have permission to access data for this warehouse.");
//            }
//        }

        Specification<MutationJournal> spec = FilterMutationJournalSpecification.withFilters(
                productName, warehouseId, status, adjustmentType, stockChangeType);

        Sort sort = Sort.by(Sort.Order.desc("updatedAt"));
        if ("ASC".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by(Sort.Order.asc("updatedAt"));
        }

        Pageable sortedPageable = PageRequest.of(
                Math.max(pageable.getPageNumber(), 0),
                Math.max(pageable.getPageSize(), 10),
                sort
        );

        return mutationJournalRepository.findAll(spec, sortedPageable)
                .map(GetAllMutationResponseDTO::fromMutationJournal);
    }


    public GetMutationResponseDTO getMutationJournalById(Long journalId) {
        String currentUserRole = Claims.getRoleFromJwt();

        if (!"Super Admin".equalsIgnoreCase(currentUserRole) &&
                !"Warehouse Admin".equalsIgnoreCase(currentUserRole)) {
            throw new AccessDeniedException("Access Denied: You do not have permission to access mutation journals.");
        }

        MutationJournal journal = mutationJournalRepository.findById(journalId)
                .orElseThrow(() -> new EntityNotFoundException("Mutation Journal not found with ID: " + journalId));

        if ("Warehouse Admin".equalsIgnoreCase(currentUserRole)) {
            List<Long> allowedWarehouseIds = Claims.getWarehouseIdsFromJwt();
            Long originWarehouseId = (journal.getOriginWarehouse() != null) ? journal.getOriginWarehouse().getId() : null;
            Long destinationWarehouseId = (journal.getDestinationWarehouse() != null) ? journal.getDestinationWarehouse().getId() : null;

            if ((originWarehouseId == null || !allowedWarehouseIds.contains(originWarehouseId))
                    && (destinationWarehouseId == null || !allowedWarehouseIds.contains(destinationWarehouseId))) {
                throw new AccessDeniedException("Access Denied: You do not have permission to access this mutation journal.");
            }
        }

        return GetMutationResponseDTO.fromMutationJournal(journal);
    }
}