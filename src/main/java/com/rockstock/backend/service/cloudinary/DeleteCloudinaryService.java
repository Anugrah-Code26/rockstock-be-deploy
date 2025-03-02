package com.rockstock.backend.service.cloudinary;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeleteCloudinaryService {

    private final Cloudinary cloudinary;

    @Transactional
    public void deleteFromCloudinary(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);

            cloudinary.uploader().destroy(publicId, Map.of());

            System.out.println("Deleted from Cloudinary: " + publicId);
        } catch (Exception e) {
            System.err.println("Error deleting file from Cloudinary: " + e.getMessage());
            throw new RuntimeException("Failed to delete image from Cloudinary", e);
        }
    }

    private String extractPublicId(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
