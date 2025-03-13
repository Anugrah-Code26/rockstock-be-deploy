package com.rockstock.backend.service.productPicture;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductPicture;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.productPicture.dto.CreateProductPictureResponseDTO;
import com.rockstock.backend.infrastructure.productPicture.dto.GetProductPicturesResponseDTO;
import com.rockstock.backend.infrastructure.productPicture.dto.UpdatePicturePositionRequestDTO;
import com.rockstock.backend.infrastructure.productPicture.dto.UpdatePicturePositionResponseDTO;
import com.rockstock.backend.infrastructure.productPicture.repository.ProductPictureRepository;
import com.rockstock.backend.service.cloudinary.CloudinaryService;
import com.rockstock.backend.service.cloudinary.DeleteCloudinaryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPictureService {
    private final ProductPictureRepository productPictureRepository;
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final DeleteCloudinaryService deleteCloudinaryService;

    @Transactional
    public CreateProductPictureResponseDTO createProductPicture(Long productId, MultipartFile file, int position) throws IOException {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        long pictureCount = productPictureRepository.countByProductId(product.getId());
        if (pictureCount >= 3) {
            throw new IllegalStateException("A product can only have up to 3 pictures.");
        }

        if (position < 1 || position > 3) {
            throw new IllegalArgumentException("Picture position must be between 1 and 3.");
        }

        if (productPictureRepository.existsByProductIdAndPosition(product.getId(), position)) {
            throw new IllegalStateException("A picture already exists at position " + position + " for this product.");
        }

        String imageUrl;
        try {
            imageUrl = cloudinaryService.uploadFile(file);
        } catch (Exception e) {
            throw new IOException("Failed to upload image to Cloudinary.", e);
        }

        ProductPicture productPicture = new ProductPicture();
        productPicture.setProduct(product);
        productPicture.setProductPictureUrl(imageUrl);
        productPicture.setPosition(position);

        ProductPicture savedPicture = productPictureRepository.save(productPicture);

        return new CreateProductPictureResponseDTO(
                savedPicture.getId(),
                savedPicture.getProductPictureUrl(),
                savedPicture.getPosition()
        );
    }

    @Transactional
    public UpdatePicturePositionResponseDTO updateProductPicturePosition(UpdatePicturePositionRequestDTO requestDTO) {
        ProductPicture productPicture = productPictureRepository
                .findByProductIdAndPictureId(requestDTO.getProductId(), requestDTO.getPictureId())
                .orElseThrow(() -> new EntityNotFoundException("Picture not found for this product or has been deleted"));

        int newPosition = requestDTO.getNewPosition();

        if (newPosition < 1 || newPosition > 3) {
            throw new IllegalArgumentException("Picture position must be between 1 and 3.");
        }

        Optional<ProductPicture> existingPictureOpt = productPictureRepository
                .findByProductIdAndPosition(requestDTO.getProductId(), newPosition);

        existingPictureOpt.ifPresent(existingPicture -> {
            existingPicture.setPosition(productPicture.getPosition());
            productPictureRepository.save(existingPicture);
        });

        productPicture.setPosition(newPosition);
        ProductPicture updatedPicture = productPictureRepository.save(productPicture);

        return new UpdatePicturePositionResponseDTO(
                updatedPicture.getId(),
                updatedPicture.getProductPictureUrl(),
                updatedPicture.getPosition()
        );
    }

    public List<GetProductPicturesResponseDTO> getAllProductPictures(Long productId) {
        // Check if the product exists
        boolean productExists = productRepository.existsById(productId);  // Assuming you have a productRepository to check if the product exists

        if (!productExists) {
            throw new DataNotFoundException("Product with ID " + productId + " not found");
        }

        List<ProductPicture> pictures = productPictureRepository.findAllByProductId(productId);

        return pictures.stream()
                .map(pic -> new GetProductPicturesResponseDTO(
                        pic.getId(),
                        pic.getProductPictureUrl(),
                        pic.getPosition()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProductPicture(Long productId, int position) {
        ProductPicture productPicture = productPictureRepository
                .findByProductIdAndPosition(productId, position)
                .orElseThrow(() -> new EntityNotFoundException("Picture not found for this product"));

        try {
            deleteCloudinaryService.deleteFromCloudinary(productPicture.getProductPictureUrl());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from Cloudinary", e);
        }

        productPictureRepository.delete(productPicture);
    }
}