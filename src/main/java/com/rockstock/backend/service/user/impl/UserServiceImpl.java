package com.rockstock.backend.service.user.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.infrastructure.user.dto.*;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Cloudinary cloudinary;

    private boolean isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.contains("jpeg") || contentType.contains("jpg") ||
                contentType.contains("png") || contentType.contains("gif"));
    }

    @SuppressWarnings("unchecked")
    private String uploadToCloudinary(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Invalid file type. Only .jpg, .jpeg, .png, and .gif are allowed.");
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 2MB.");
        }

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public User getUserProfile() {
        Long userId = Claims.getUserIdFromJwt();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user;
    }

    @Override
    public List<GetAllUsersDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(GetAllUsersDTO::fromEntity).toList();
    }

    @Override
    @Transactional
    public UserPublicDetailsDTO getUserDetails(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserPublicDetailsDTO userDetails = new UserPublicDetailsDTO();
        userDetails.setUserId(user.getId());
        userDetails.setPhotoProfileUrl(user.getPhotoProfileUrl());
        userDetails.setFullName(user.getFullname());
        userDetails.setEmail(user.getEmail());

        return userDetails;
    }



    @Transactional
    public User updateUserProfile(Long userId, UpdateProfileRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Old Fullname: " + user.getFullname());
        System.out.println("New Fullname: " + request.getFullname());

        user.setFullname(request.getFullname());
        user.setBirthDate(request.getBirthDate());
        user.setGender(request.getGender());

        if (request.getPhotoProfileUrl() != null && !request.getPhotoProfileUrl().isEmpty()) {
            user.setPhotoProfileUrl(request.getPhotoProfileUrl());
        }

        System.out.println("Updated Fullname: " + user.getFullname());  // Ensure this is logged after update

        userRepository.saveAndFlush(user); // Ensure the save is flushed to DB
        return user;
    }


    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPassword() == null) {
            throw new RuntimeException("Password change not allowed for social login users");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UploadAvatarResponseDTO uploadAvatar(Long userId, MultipartFile file) {
        String imageUrl = uploadImage(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        updateProfilePicture(user, imageUrl);

        return new UploadAvatarResponseDTO(imageUrl);
    }

    private String uploadImage(MultipartFile file) {
        try {
            return uploadToCloudinary(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary: " + e.getMessage());
        }
    }

    private void updateProfilePicture(User user, String imageUrl) {
        user.setPhotoProfileUrl(imageUrl);
        userRepository.save(user);
    }



    @Override
    public void updateEmail(Long userId, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Email is already in use");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(newEmail);
        user.setIsVerified(false);
        userRepository.save(user);


        // TODO: Send verification email with expiration of 1 hour
    }

    @Override
    public void resendEmailVerification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsVerified()) {
            throw new RuntimeException("User is already verified");
        }

        // TODO: Implement email verification resend logic
    }
}
