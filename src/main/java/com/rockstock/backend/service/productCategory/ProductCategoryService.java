package com.rockstock.backend.service.productCategory;

import com.rockstock.backend.entity.product.ProductCategory;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.productCategory.dto.*;
import com.rockstock.backend.infrastructure.productCategory.repository.ProductCategoryRepository;
import com.rockstock.backend.service.cloudinary.CloudinaryService;
import com.rockstock.backend.service.cloudinary.DeleteCloudinaryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final DeleteCloudinaryService deleteCloudinaryService;

    @Transactional
    public CreateProductCategoryResponseDTO createProductCategory(CreateProductCategoryRequestDTO createProductCategoryRequestDTO, MultipartFile file) throws IOException {
        String categoryName = createProductCategoryRequestDTO.getCategoryName().trim();

        if (productCategoryRepository.existsByCategoryName(categoryName)) {
            throw new IllegalArgumentException("Category name already exists.");
        }

        String imageUrl;
            try {
                imageUrl = cloudinaryService.uploadFile(file);
            } catch (Exception e) {
                throw new IOException("Failed to upload image to Cloudinary.", e);
        }

        // Create new ProductCategory entity
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName(categoryName);
        productCategory.setCategoryPicture(imageUrl); // Store uploaded image URL

        ProductCategory savedCategory = productCategoryRepository.save(productCategory);

        return new CreateProductCategoryResponseDTO(
                savedCategory.getId(),
                savedCategory.getCategoryName(),
                savedCategory.getCategoryPicture()
        );
    }

    @Transactional
    public CreateProductCategoryResponseDTO updateProductCategory(UpdateProductCategoryRequestDTO requestDTO) throws IOException {
        ProductCategory productCategory = productCategoryRepository.findByCategoryId(requestDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + requestDTO.getCategoryId()));

        // Update category name if provided
        if (requestDTO.getCategoryName() != null && !requestDTO.getCategoryName().isBlank() &&
                !productCategory.getCategoryName().equalsIgnoreCase(requestDTO.getCategoryName())) {

            if (productCategoryRepository.existsByCategoryName(requestDTO.getCategoryName())) {
                throw new IllegalArgumentException("Category name already exists: " + requestDTO.getCategoryName());
            }
            productCategory.setCategoryName(requestDTO.getCategoryName());
        }

        // Upload new picture if provided
        if (requestDTO.getFile() != null && !requestDTO.getFile().isEmpty()) {
            // Delete old picture from Cloudinary if it exists
            if (productCategory.getCategoryPicture() != null) {
                deleteCloudinaryService.deleteFromCloudinary(productCategory.getCategoryPicture());
            }

            // Upload new picture
            String imageUrl = cloudinaryService.uploadFile(requestDTO.getFile());
            productCategory.setCategoryPicture(imageUrl);
        }

        ProductCategory updatedCategory = productCategoryRepository.save(productCategory);

        return new CreateProductCategoryResponseDTO(
                updatedCategory.getId(),
                updatedCategory.getCategoryName(),
                updatedCategory.getCategoryPicture()
        );
    }

    @Transactional
    public void softDeleteProductCategory(Long categoryId) {
        ProductCategory productCategory = productCategoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        // Check if there are any products using this category
        boolean hasProducts = productRepository.existsByProductCategory(productCategory);
        if (hasProducts) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete category: Products are still using this category.");
        }

        // Perform soft delete by setting deletedAt timestamp
        productCategory.setDeletedAt(OffsetDateTime.now());
        productCategoryRepository.save(productCategory);
    }

    public void restoreProductCategory(Long categoryId) {
        ProductCategory productCategory = productCategoryRepository.findDeletedCategoryById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found or not deleted"));

        productCategory.setDeletedAt(null);
        productCategoryRepository.save(productCategory);
    }

    public Page<HomeProductCategoryDTO> getAllCategories(String categoryName, Pageable pageable) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return productCategoryRepository.findAllActiveCategories(null, pageable)
                    .map(category -> new HomeProductCategoryDTO(
                            category.getId(),
                            category.getCategoryName(),
                            category.getCategoryPicture()));
        }

        return productCategoryRepository.findAllActiveCategories(categoryName, pageable)
                .map(category -> new HomeProductCategoryDTO(
                        category.getId(),
                        category.getCategoryName(),
                        category.getCategoryPicture()));
    }

    public GetProductCategoryResponseDTO getProductCategoryById(Long categoryId) {
        ProductCategory productCategory = productCategoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Product Category with ID " + categoryId + " not found"));

        // Return a DTO instead of the entity
        return new GetProductCategoryResponseDTO(productCategory.getId(), productCategory.getCategoryPicture(), productCategory.getCategoryName());
    }
}