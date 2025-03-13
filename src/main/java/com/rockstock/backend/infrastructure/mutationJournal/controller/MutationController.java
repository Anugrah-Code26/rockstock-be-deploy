package com.rockstock.backend.infrastructure.mutationJournal.controller;

import com.rockstock.backend.entity.stock.MutationStatus;
import com.rockstock.backend.entity.stock.StockAdjustmentType;
import com.rockstock.backend.entity.stock.StockChangeType;
import com.rockstock.backend.infrastructure.mutationJournal.dto.*;
import com.rockstock.backend.service.mutation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/mutations")
@RequiredArgsConstructor
public class MutationController {
    private final CreateMutationRequestService createMutationRequestService;
    private final AdjustStockService adjustStockService;
    private final GetMutationJournalService getMutationJournalService;
    private final ProcessMutationRequestService processMutationRequestService;
    private final ConfirmMutationStockService confirmMutationStockService;
    private final CancelMutationRequestService cancelMutationRequestService;

    @PostMapping("/{warehouseId}/{productId}/request")
    public ResponseEntity<Map<String, String>> requestStockMutation(
            @PathVariable Long warehouseId,
            @PathVariable Long productId,
            @Valid @RequestBody MutationRequestDTO requestDTO) {

        createMutationRequestService.createMutationRequest(warehouseId, productId, requestDTO);
        return ResponseEntity.ok(Map.of("message", "Stock mutation request submitted successfully."));
    }

    @PatchMapping("/{journalId}/{warehouseId}/cancel")
    public ResponseEntity<Map<String, String>> cancelMutationRequest(
            @PathVariable Long journalId,
            @PathVariable Long warehouseId,
            @Valid @RequestBody CancelRequestDTO cancelRequestDTO) {

        String message = cancelMutationRequestService.cancelPendingMutationRequest(journalId, warehouseId, cancelRequestDTO);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PatchMapping("/{journalId}/{warehouseId}/process")
    public ResponseEntity<Map<String, String>> processMutationRequest(
            @PathVariable Long journalId,
            @PathVariable Long warehouseId,
            @Valid @RequestBody ProcessRequestDTO processDTO) {

        String message = processMutationRequestService.processMutationRequest(journalId, warehouseId, processDTO.isApproved(), processDTO);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PatchMapping("/{journalId}/{warehouseId}/confirm")
    public ResponseEntity<Map<String, String>> confirmReceivedStock(
            @PathVariable Long journalId,
            @PathVariable Long warehouseId,
            @Valid @RequestBody ConfirmStockRequestDTO confirmDTO) {

        String message = confirmMutationStockService.confirmMutationStockService(journalId, warehouseId, confirmDTO.isCompleted(), confirmDTO);

        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/{warehouseId}/{productId}/adjust")
    public ResponseEntity<String> adjustStock(
            @PathVariable Long warehouseId,
            @PathVariable Long productId,
            @RequestBody StockAdjustmentRequestDTO stockAdjustmentDTO) {
        try {
            adjustStockService.adjustStockRequest(warehouseId, productId, stockAdjustmentDTO);
            return ResponseEntity.ok("Stock adjusted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<GetAllMutationResponseDTO>> getAllMutationJournals(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) MutationStatus status,
            @RequestParam(required = false) StockAdjustmentType adjustmentType,
            @RequestParam(required = false) StockChangeType stockChangeType,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<GetAllMutationResponseDTO> result = getMutationJournalService.getAllMutationJournals(
                productName, warehouseId, status, adjustmentType, stockChangeType, sortDirection, pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{journalId}")
    public GetMutationResponseDTO getMutationJournalById(@PathVariable Long journalId) {
        return getMutationJournalService.getMutationJournalById(journalId);
    }
}