package com.rockstock.backend.infrastructure.user.controller;

import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.infrastructure.user.dto.*;
import com.rockstock.backend.service.user.UserService;
import com.rockstock.backend.service.user.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResetPasswordService resetPasswordService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        User userProfile = userService.getUserProfile();
        return ApiResponse.success(HttpStatus.OK.value(), "User profile retrieved successfully", userProfile);
    }

    @GetMapping
    public ResponseEntity<List<GetAllUsersDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateProfileRequestDTO request) {
        Long userId = Claims.getUserIdFromJwt();
        User updatedUser = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User profile updated successfully", updatedUser));
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = Claims.getUserIdFromJwt();
        UploadAvatarResponseDTO response = userService.uploadAvatar(userId, file);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Avatar uploaded successfully", response));
    }


    // Reset password request endpoint: send an email to reset the password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        try {
            resetPasswordService.sendResetPasswordEmail(request);
            return ResponseEntity.ok(Map.of("message", "Reset password link sent"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm-reset-password")
    public ResponseEntity<?> confirmResetPassword(@RequestBody @Valid ConfirmResetPasswordDTO request) {
        System.out.println("Received token: " + request.getResetToken());
        System.out.println("New password: " + request.getNewPassword());
        System.out.println("Confirm reset password endpoint hit!");
        resetPasswordService.confirmResetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
    }


    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestParam String newEmail) {
        Long userId = Claims.getUserIdFromJwt();
        userService.updateEmail(userId, newEmail);
        return ApiResponse.success("Email updated successfully. Please verify your new email.");
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<?> resendEmailVerification() {
        Long userId = Claims.getUserIdFromJwt();
        userService.resendEmailVerification(userId);
        return ApiResponse.success("Verification email resent successfully.");
    }
}
