package com.rockstock.backend.infrastructure.warehouseStock.controller;

import com.rockstock.backend.infrastructure.warehouseStock.dto.AllWarehouseStockResponseDTO;
import com.rockstock.backend.infrastructure.warehouseStock.dto.WarehouseStockRequestDTO;
import com.rockstock.backend.infrastructure.warehouseStock.dto.WarehouseStockResponseDTO;
import com.rockstock.backend.service.warehouseStock.WarehouseStockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/stocks")
@RequiredArgsConstructor
public class WarehouseStockController {

    private final WarehouseStockService warehouseStockService;

    @PostMapping("/create")
    public ResponseEntity<?> createWarehouseStock(@RequestBody WarehouseStockRequestDTO requestDTO) {
        try {
            WarehouseStockResponseDTO response = warehouseStockService.createWarehouseStock(requestDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "statusCode", 409,
                    "message", e.getMessage(),
                    "success", false
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "statusCode", 404,
                    "message", e.getMessage(),
                    "success", false
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "statusCode", 500,
                    "message", "An unexpected error occurred",
                    "success", false
            ));
        }
    }

    @PatchMapping("/{stockId}/delete")
    public ResponseEntity<String> softDeleteWarehouseStock(@PathVariable Long stockId) {
        if (stockId == null) {
            return ResponseEntity.badRequest().body("stockId must be provided.");
        }

        warehouseStockService.softDeleteWarehouseStock(stockId);
        return ResponseEntity.ok("WarehouseStock soft deleted successfully.");
    }

    @PatchMapping("/{stockId}/restore")
    public ResponseEntity<String> restoreWarehouseStock(@PathVariable Long stockId) {
        if (stockId == null) {
            return ResponseEntity.badRequest().body("stockId must be provided.");
        }

        warehouseStockService.restoreWarehouseStock(stockId);
        return ResponseEntity.ok("WarehouseStock restored successfully.");
    }

    @GetMapping
    public ResponseEntity<?> getAllWarehouseStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("product.productName").ascending());

        if ("DESC".equalsIgnoreCase(sortDirection)) {
            pageable = PageRequest.of(page, size, Sort.by("product.productName").descending());
        }

        Page<AllWarehouseStockResponseDTO> warehouseStocks = warehouseStockService.getFilteredWarehouseStocks(productName, warehouseId, pageable);

        if (warehouseStocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("statusCode", 404, "message", "No warehouse stocks found", "success", false));
        }

        return ResponseEntity.ok(Map.of("statusCode", 200, "message", "Fetched warehouse stocks", "success", true, "data", warehouseStocks));
    }


    @GetMapping("/{stockId}")
    public ResponseEntity<WarehouseStockResponseDTO> getWarehouseStockById(@PathVariable Long stockId) {
        WarehouseStockResponseDTO response = warehouseStockService.getWarehouseStockById(stockId);

        return ResponseEntity.ok(response);
    }
}