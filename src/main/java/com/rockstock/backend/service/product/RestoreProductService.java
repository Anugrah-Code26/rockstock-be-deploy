package com.rockstock.backend.service.product;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.product.ProductPicture;
import com.rockstock.backend.entity.stock.WarehouseStock;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.productPicture.repository.ProductPictureRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestoreProductService {
    private final ProductRepository productRepository;
    private final ProductPictureRepository productPictureRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    @Transactional
    public void restoreProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found or not deleted"));

        product.setDeletedAt(null);
        productRepository.save(product);

        List<ProductPicture> productPictures = productPictureRepository.findByProductIdAndDeletedAtIsNotNull(id);
        for (ProductPicture picture : productPictures) {
            picture.setDeletedAt(null);
            productPictureRepository.save(picture);
        }

        List<WarehouseStock> warehouseStocks = warehouseStockRepository.findByProductIdAndDeletedAtIsNotNull(id);
        for (WarehouseStock stock : warehouseStocks) {
            stock.setDeletedAt(null);
            warehouseStockRepository.save(stock);
        }
    }
}
