package com.rockstock.backend.infrastructure.mutationJournal.controller;

import com.rockstock.backend.entity.stock.MutationStatus;
import com.rockstock.backend.infrastructure.mutationJournal.dto.*;
import com.rockstock.backend.service.mutation.MutationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/mutations")
@RequiredArgsConstructor
public class MutationController {

    private final MutationService mutationService;

    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestStockMutation(
            @Valid @RequestBody MutationRequestDTO requestDTO) {
        mutationService.mutationRequestService(requestDTO);
        return ResponseEntity.ok(Map.of("message", "Stock mutation request submitted successfully."));
    }

    @PatchMapping("/{journalId}/process")
    public ResponseEntity<Map<String, String>> processMutationRequest(
            @PathVariable Long journalId,
            @Valid @RequestBody ProcessRequestDTO processDTO) {

        String message = mutationService.processMutationRequestService(journalId, processDTO.isApproved(), processDTO);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PatchMapping("/{journalId}/confirm")
    public ResponseEntity<Map<String, String>> confirmReceivedStock(
            @PathVariable Long journalId,
            @Valid @RequestBody ConfirmStockRequestDTO confirmDTO) {

        String message = mutationService.confirmReceivedStockService(journalId, confirmDTO.isCompleted(), confirmDTO);
        return ResponseEntity.ok(Map.of("message", message));
    }

    // Adjust stock quantity directly (Separate feature from mutations)
    @PostMapping("/{warehouseId}/{productId}/adjust")
    public ResponseEntity<String> adjustStock(
            @PathVariable Long warehouseId,
            @PathVariable Long productId,
            @RequestBody StockAdjustmentRequestDTO stockAdjustmentDTO) {
        try {
            mutationService.adjustStock(warehouseId, productId, stockAdjustmentDTO);
            return ResponseEntity.ok("Stock adjusted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public Page<GetMutationResponseDTO> getAllMutationJournals(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) MutationStatus mutationStatus,
            @RequestParam(defaultValue = "asc") String sortByUpdatedAt,
            Pageable pageable
    ) {
        return mutationService.getAllMutationJournals(productId, warehouseName, mutationStatus, sortByUpdatedAt, pageable);
    }

    @GetMapping("/{journalId}")
    public GetMutationResponseDTO getMutationJournalById(@PathVariable Long journalId) {
        return mutationService.getMutationJournalById(journalId);
    }
}