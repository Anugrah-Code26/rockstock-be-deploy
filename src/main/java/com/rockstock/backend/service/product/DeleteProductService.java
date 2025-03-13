package com.rockstock.backend.service.product;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductPicture;
import com.rockstock.backend.entity.product.ProductStatus;
import com.rockstock.backend.entity.stock.WarehouseStock;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.productPicture.repository.ProductPictureRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import com.rockstock.backend.service.cloudinary.DeleteCloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteProductService {
    private final ProductRepository productRepository;
    private final ProductPictureRepository productPictureRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final DeleteCloudinaryService deleteCloudinaryService;

    @Transactional
    public void softDeleteProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only draft products can be deleted");
        }

        product.setDeletedAt(OffsetDateTime.now());
        productRepository.save(product);

        List<ProductPicture> productPictures = productPictureRepository.findAllByProductId(id);
        for (ProductPicture picture : productPictures) {
            picture.setDeletedAt(OffsetDateTime.now());
            productPictureRepository.save(picture);
        }

        List<WarehouseStock> warehouseStocks = warehouseStockRepository.findAllByProductId(id);
        for (WarehouseStock stock : warehouseStocks) {
            stock.setDeletedAt(OffsetDateTime.now());
            warehouseStockRepository.save(stock);
        }
    }

    @Transactional
    public void hardDeleteProduct(Long productId) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getStatus() != ProductStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only draft products can be deleted");
        }

        // Step 1: Delete related ProductPictures and images from Cloudinary
        deleteProductPicturesAndCloudinary(product);

        // Step 2: Hard delete the product from the database
        productRepository.delete(product);
    }

    private void deleteProductPicturesAndCloudinary(Product product) {
        // Fetch all ProductPictures associated with the product
        List<ProductPicture> pictures = productPictureRepository.findByProductIdAndDeletedAtIsNull(product.getId());

        // Loop through each picture and delete from Cloudinary
        for (ProductPicture picture : pictures) {
            String imageUrl = picture.getProductPictureUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Step 1: Delete image from Cloudinary
                deleteCloudinaryService.deleteFromCloudinary(imageUrl);
            }

            // Step 2: Delete the ProductPicture from the database
            productPictureRepository.delete(picture);
        }
    }
}
