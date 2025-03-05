package com.rockstock.backend.service.user.auth.impl;//package com.rockstock.backend.service.user.auth.impl;
//
//import com.rockstock.backend.entity.User;
//import com.rockstock.backend.infrastructure.system.security.TokenService;
//import com.rockstock.backend.infrastructure.user.repository.UserRepository;
//import com.rockstock.backend.service.user.auth.PasswordResetService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class PasswordResetUsecaseImpl implements PasswordResetService {
//    private final UserRepository userRepository;
//    private final TokenService tokenService;
//    private final PasswordEncoder passwordEncoder;
//
//    public PasswordResetUsecaseImpl(UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.tokenService = tokenService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public void sendResetPasswordEmail(String email) {
//        User user = userRepository.findByEmailContainsIgnoreCase(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        String token = tokenService.generateResetPasswordToken(user);
//        // Logic to send email (e.g., with a mail service)
//    }
//
//    @Override
//    public void resetPassword(String resetToken, String newPassword) {
//        String email = tokenService.validateResetPasswordToken(resetToken);
//        User user = userRepository.findByEmailContainsIgnoreCase(email)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//    }
//}
