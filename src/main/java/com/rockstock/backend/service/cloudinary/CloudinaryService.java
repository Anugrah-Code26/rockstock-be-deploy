package com.rockstock.backend.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@AllArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    private boolean isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.contains("jpeg") || contentType.contains("jpg") ||
                contentType.contains("png") || contentType.contains("gif"));
    }

    @SuppressWarnings("unchecked")
    private String uploadToCloudinary(MultipartFile file) throws IOException {
        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Invalid file type. Only .jpg, .jpeg, .png, and .gif are allowed.");
        }

        if (file.getSize() > 1 * 1024 * 1024) { // 1MB limit
            throw new IllegalArgumentException("File size must be less than 1MB.");
        }

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url"); // Cloudinary URL
    }

    public String uploadFile(MultipartFile file) throws IOException {
        return uploadToCloudinary(file);
    }
}
