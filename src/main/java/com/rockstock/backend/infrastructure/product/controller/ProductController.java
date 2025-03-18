package com.rockstock.backend.infrastructure.product.controller;

import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.entity.product.ProductStatus;
import com.rockstock.backend.infrastructure.product.dto.*;
import com.rockstock.backend.service.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
        return ResponseEntity.ok(response); // Change CREATED (201) to OK (200)
    }

    @PatchMapping("/{id}/create")
    public ResponseEntity<UpdateProductResponseDTO> createProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequestDTO updateProductRequestDTO) {

        UpdateProductResponseDTO response = updateProductService.updateProductToActive(id, updateProductRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/draft")
    public ResponseEntity<UpdateProductResponseDTO> updateDraftProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequestDTO updateProductRequestDTO) {

        UpdateProductResponseDTO response = updateProductService.updateProductToDraft(id, updateProductRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/edit")
    public ResponseEntity<EditProductResponseDTO> editProduct(
            @PathVariable Long id,
            @RequestBody EditProductRequestDTO editProductRequestDTO) {

        EditProductResponseDTO response = editProductService.editProduct(id, editProductRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<GetListProductResponseDTO>> getAllListProducts() {
        return ResponseEntity.ok(getProductService.getAllListProducts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<GetProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(getProductService.getProductById(id));
    }

    @GetMapping("/active")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<GetAllProductResponseDTO>> getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "updatedAt") String sortField,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        return ResponseEntity.ok(getProductService.getAllProducts(page, size, name, categoryId, sortField, sortDirection, new ProductStatus[]{ProductStatus.ACTIVE}));
    }

    @GetMapping("/draft")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<GetAllProductResponseDTO>> getDraftProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "updatedAt") String sortField,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        return ResponseEntity.ok(getProductService.getAllProducts(page, size, name, categoryId, sortField, sortDirection, new ProductStatus[] { ProductStatus.DRAFT }));
    }

    @PatchMapping("/{id}/delete")
    public ResponseEntity<String> softDeleteProduct(@PathVariable Long id) {
        deleteProductService.softDeleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> hardDeleteProduct(@PathVariable Long id) {
        deleteProductService.hardDeleteProduct(id);
        return ResponseEntity.ok("Draft deleted successfully");
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<String> restoreProduct(@PathVariable Long id) {
        restoreProductService.restoreProduct(id);
        return ResponseEntity.ok("Product restored successfully");
    }
}