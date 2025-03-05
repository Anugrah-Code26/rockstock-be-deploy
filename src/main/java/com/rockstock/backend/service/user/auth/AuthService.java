package com.rockstock.backend.service.user.auth;//package com.rockstock.backend.service.user.auth;
//
//import com.rockstock.backend.infrastructure.user.auth.dto.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final PasswordEncoder passwordEncoder; // Gunakan untuk enkripsi password
//
//    public LoginResponseDTO authenticateUser(LoginRequestDTO req) {
//        // Implementasi login (nanti sesuaikan dengan database)
//        return new LoginResponseDTO("dummyToken", "Login berhasil");
//    }
//
//    public void register(RegisterRequestDTO req) {
//        // Implementasi registrasi user baru
//        System.out.println("User " + req.getEmail() + " registered.");
//    }
//
//    public void verifyEmail(String token) {
//        // Implementasi verifikasi email
//        System.out.println("Verifying token: " + token);
//    }
//
//    public void sendResetPasswordEmail(ResetPasswordRequestDTO req) {
//        // Implementasi kirim email reset password
//        System.out.println("Sending reset password email to: " + req.getEmail());
//    }
//
//    public void resetPassword(ConfirmResetPasswordDTO req) {
//        // Implementasi reset password
//        System.out.println("Password reset for token: " + req.getToken());
//    }
//}
