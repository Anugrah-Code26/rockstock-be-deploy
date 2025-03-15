package com.rockstock.backend.infrastructure.productCategory.controller;

import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.infrastructure.productCategory.dto.*;
import com.rockstock.backend.service.productCategory.ProductCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/categories")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

//    @PreAuthorize("hasRole('SUPER ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateProductCategoryResponseDTO> createCategory(
//            Long sellerId = Claims.getUserIdFromJwt();
            @RequestParam("categoryName") String categoryName,
            @RequestParam("file") MultipartFile file) throws IOException {

        CreateProductCategoryRequestDTO requestDTO = new CreateProductCategoryRequestDTO(categoryName, null);
        CreateProductCategoryResponseDTO responseDTO = productCategoryService.createProductCategory(requestDTO, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

//    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping(value = "/{categoryId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreateProductCategoryResponseDTO> updateCategory(
            @PathVariable Long categoryId,
            @ModelAttribute UpdateProductCategoryRequestDTO requestDTO) throws IOException {

        requestDTO.setCategoryId(categoryId);

        CreateProductCategoryResponseDTO responseDTO = productCategoryService.updateProductCategory(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{categoryId}/delete")
    public ResponseEntity<String> softDeleteProductCategory(@PathVariable Long categoryId) {
        productCategoryService.softDeleteProductCategory(categoryId);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @PatchMapping("/{categoryId}/restore")
    public ResponseEntity<String> restoreProduct(@PathVariable Long categoryId) {
        productCategoryService.restoreProductCategory(categoryId);
        return ResponseEntity.ok("Category restored successfully");
    }

    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllListProductCategories() {
        return ApiResponse.success(HttpStatus.OK.value(), "Get all product categories success!",productCategoryService.getAllListProductCategories());
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryName) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryName").ascending());
        Page<HomeProductCategoryDTO> categories = productCategoryService.getAllCategories(categoryName, pageable);

        if (categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("statusCode", 404, "message", "No categories found", "success", false));
        }

        return ResponseEntity.ok(Map.of("statusCode", 200, "message", "Fetched categories", "success", true, "data", categories));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<GetProductCategoryResponseDTO>> getCategoryById(
            @PathVariable Long categoryId) {
        GetProductCategoryResponseDTO category = productCategoryService.getProductCategoryById(categoryId);
        return ApiResponse.success("Product found", category);
    }
}