package com.rockstock.backend.infrastructure.product.controller;

import com.rockstock.backend.infrastructure.product.dto.*;
import com.rockstock.backend.service.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductService createProductService;
    private final UpdateProductService updateProductService;
    private final EditProductService editProductService;
    private final GetProductService getProductService;
    private final DeleteProductService deleteProductService;
    private final RestoreProductService restoreProductService;
    
    @PostMapping("/draft")
    public ResponseEntity<CreateProductResponseDTO> createDraftProduct() {
        CreateProductResponseDTO response = createProductService.createDraftProduct();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<UpdateProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequestDTO updateProductRequestDTO) {

        UpdateProductResponseDTO response = updateProductService.updateProduct(id, updateProductRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<EditProductResponseDTO> editProduct(
            @PathVariable Long id,
            @RequestBody EditProductRequestDTO editProductRequestDTO) {

        EditProductResponseDTO response = editProductService.editProduct(id, editProductRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<GetProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(getProductService.getProductById(id));
    }

    // ✅ Get all products with filters, sorting, and pagination
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<GetAllProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "updatedAt") String sortField,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        return ResponseEntity.ok(getProductService.getAllProducts(page, size, name, category, sortField, sortDirection));
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<String> softDeleteProduct(@PathVariable Long id) {
        deleteProductService.softDeleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> hardDeleteProduct(@PathVariable Long id) {
        deleteProductService.hardDeleteProduct(id);
        return ResponseEntity.ok("Draft deleted successfully");
    }

    @PatchMapping("/restore/{id}")
    public ResponseEntity<String> restoreProduct(@PathVariable Long id) {
        restoreProductService.restoreProduct(id);
        return ResponseEntity.ok("Product restored successfully");
    }
}