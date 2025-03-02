package com.rockstock.backend.infrastructure.mutation.controller;

import com.rockstock.backend.infrastructure.mutation.dto.MutationRequestDTO;
import com.rockstock.backend.service.mutation.MutationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/mutations")
@RequiredArgsConstructor
public class MutationController {

    private final MutationService mutationService;

    @PostMapping("/request")
    public ResponseEntity<String> requestStockMutation(@RequestBody MutationRequestDTO requestDTO) {
        mutationService.mutationRequestService(requestDTO);
        return ResponseEntity.ok("Stock mutation request submitted successfully.");
    }

    // Approve or Cancel the stock mutation (Origin warehouse admin approves/cancels)
    @PutMapping("/{journalId}/process")
    public ResponseEntity<String> processMutationRequest(
            @PathVariable Long journalId,
            @RequestParam boolean isApproved) {

        mutationService.processMutationRequestService(journalId, isApproved);
        return ResponseEntity.ok(isApproved ? "Stock mutation approved." : "Stock mutation canceled.");
    }

    // Confirm that stock has been received successfully (Destination warehouse confirms)
    @PutMapping("/{journalId}/confirm")
    public ResponseEntity<String> confirmReceivedStock(
            @PathVariable Long journalId,
            @RequestParam boolean isSuccessful,
            @RequestParam String details) {

        mutationService.confirmReceivedStock(journalId, isSuccessful, details);
        return ResponseEntity.ok(isSuccessful ? "Stock mutation completed successfully." : "Stock mutation failed.");
    }

    // Adjust stock quantity directly (Separate feature from mutations)
    @PostMapping("/adjust")
    public ResponseEntity<String> adjustStock(
            @RequestParam Long warehouseId,
            @RequestParam Long productId,
            @RequestParam Long quantityChange,
            @RequestParam String adjustmentType,
            @RequestParam String details) {

        mutationService.adjustStock(warehouseId, productId, quantityChange, adjustmentType, details);
        return ResponseEntity.ok("Stock adjustment completed successfully.");
    }
}