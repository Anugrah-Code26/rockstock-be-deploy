package com.rockstock.backend.infrastructure.productPicture.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.infrastructure.productPicture.dto.*;
import com.rockstock.backend.service.productPicture.ProductPictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pictures")
@RequiredArgsConstructor
public class ProductPictureController {
    private final ProductPictureService productPictureService;

    @PostMapping("/{productId}/create")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<CreateProductPictureResponseDTO>> createProductPicture(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestPart("request") String requestJson) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CreateProductPictureRequestDTO requestDTO = objectMapper.readValue(requestJson, CreateProductPictureRequestDTO.class);

        // Set the productId from path variable
        requestDTO.setProductId(productId);

        CreateProductPictureResponseDTO response = productPictureService.createProductPicture(requestDTO, file);
        return ApiResponse.success("Create new category success", response);
    }

    // 📌 Update Picture Position (Drag & Drop Reordering)
    @PatchMapping("/{productId}/{pictureId}/position")
    public ResponseEntity<UpdatePicturePositionResponseDTO> updateProductPicturePosition(
            @PathVariable Long productId,
            @PathVariable Long pictureId,
            @RequestBody UpdatePicturePositionRequestDTO requestDTO) {

        // Ensure requestDTO gets productId and pictureId from path variables
        requestDTO.setProductId(productId);
        requestDTO.setPictureId(pictureId);

        UpdatePicturePositionResponseDTO responseDTO = productPictureService.updateProductPicturePosition(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<List<GetProductPicturesResponseDTO>>> getAllProductPictures(@PathVariable Long productId) {
        List<GetProductPicturesResponseDTO> pictures = productPictureService.getAllProductPictures(productId);

        if (pictures.isEmpty()) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), "No pictures found");
        }

        return ApiResponse.success("Pictures found", pictures);
    }

    @DeleteMapping("/{productId}/{pictureId}/pictures")
    public ResponseEntity<String> deleteProductPicture(
            @PathVariable Long productId,
            @PathVariable Long pictureId) {

        productPictureService.deleteProductPicture(productId, pictureId);
        return ResponseEntity.ok("Product picture deleted successfully");
    }
}