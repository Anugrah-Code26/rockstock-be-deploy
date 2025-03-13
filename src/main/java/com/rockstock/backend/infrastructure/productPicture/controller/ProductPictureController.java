package com.rockstock.backend.infrastructure.productPicture.controller;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.infrastructure.productPicture.dto.CreateProductPictureResponseDTO;
import com.rockstock.backend.infrastructure.productPicture.dto.GetProductPicturesResponseDTO;
import com.rockstock.backend.infrastructure.productPicture.dto.UpdatePicturePositionRequestDTO;
import com.rockstock.backend.infrastructure.productPicture.dto.UpdatePicturePositionResponseDTO;
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

    @PostMapping("/{productId}/{position}/upload")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<CreateProductPictureResponseDTO>> createProductPicture(
            @PathVariable Long productId,
            @PathVariable Integer position,
            @RequestParam("file") MultipartFile file) throws IOException {

        CreateProductPictureResponseDTO response = productPictureService.createProductPicture(productId, file, position);
        return ApiResponse.success("Create new product picture success", response);
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
        try {
            List<GetProductPicturesResponseDTO> pictures = productPictureService.getAllProductPictures(productId);

            // If no pictures are found, still return 200 OK with an empty array
            if (pictures.isEmpty()) {
                return ApiResponse.success("No pictures found", pictures);  // You can change the message as needed
            }

            return ApiResponse.success("Pictures found", pictures);

        } catch (DataNotFoundException ex) {
            return ApiResponse.failed(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred");
        }
    }

    @DeleteMapping("/{productId}/{position}/delete")
    public ResponseEntity<String> deleteProductPicture(
            @PathVariable Long productId,
            @PathVariable int position) {

        productPictureService.deleteProductPicture(productId, position);
        return ResponseEntity.ok("Product picture deleted successfully");
    }
}