package com.rockstock.backend.service.user;

import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.user.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User getUserProfile();
    User updateUserProfile(Long userId, UpdateProfileRequestDTO request);
    void changePassword(Long userId, ChangePasswordRequest request);
    UploadAvatarResponseDTO uploadAvatar(Long userId, MultipartFile file);
    void updateEmail(Long userId, String newEmail);
    void resendEmailVerification(Long userId);
    List<GetAllUsersDTO> getAllUsers();
    UserPublicDetailsDTO getUserDetails(Long userId, Pageable pageable);
}