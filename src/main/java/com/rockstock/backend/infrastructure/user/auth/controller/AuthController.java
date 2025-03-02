package com.rockstock.backend.infrastructure.user.auth.controller;

import com.rockstock.backend.infrastructure.user.auth.CreateUserService;
import com.rockstock.backend.service.user.auth.*;
import com.rockstock.backend.service.user.UserService;
import com.rockstock.backend.service.user.auth.EmailVerificationService;
import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.infrastructure.user.auth.dto.*;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginService loginService;
    private final TokenRefreshService tokenRefreshService;
    private final LogoutService logoutService;
    private OAuthService oAuthService = null;
    @Autowired
    private CreateUserService createUserService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    public AuthController(LoginService loginService, TokenRefreshService tokenRefreshService,
                          TokenBlacklistService tokenBlacklistService, LogoutService logoutService) {
        this.loginService = loginService;
        this.tokenRefreshService = tokenRefreshService;
        this.logoutService = logoutService;
        this.oAuthService = oAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequestDTO req) {
        var userResponse = createUserService.createUser(req);
        return ApiResponse.success(HttpStatus.OK.value(), "Registration successful. Please verify your email to activate your account.", userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequestDTO req) {
        return ApiResponse.success("Login successful", loginService.authenticateUser(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Validated @RequestBody LogoutRequestDTO req) {
        var accessToken = Claims.getJwtTokenString();
        req.setAccessToken(accessToken);
        return ApiResponse.success("Logout successful", logoutService.logoutUser(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        String tokenType = Claims.getTokenTypeFromJwt();
        if (!"REFRESH".equals(tokenType)) {
            return ApiResponse.failed(HttpStatus.UNAUTHORIZED.value(), "Invalid token type for refresh");
        }
        String token = Claims.getJwtTokenString();
        return ApiResponse.success("Refresh successful", tokenRefreshService.refreshAccessToken(token));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        emailVerificationService.verifyEmail(token);  // Verifikasi email
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email successfully verified. You can now set your password.");
        return ResponseEntity.ok(response);  // Kembalikan dalam format JSON
    }

//    @GetMapping("/me")
//    public ResponseEntity<?> getUserStatus(@AuthenticationPrincipal UserDetails userDetails) {
//        var user = userService.findByEmail(userDetails.getUsername());
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
//        }
//
//        return ResponseEntity.ok(Map.of(
//                "fullname", user.getFullname(),
//                "isVerified", user.isVerified()
//        ));
//    }


    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@RequestBody @Valid ResendVerificationRequestDTO request) {
        emailVerificationService.resendVerificationEmail(request.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Verification email has been resent");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/setup-password")
    public ResponseEntity<?> setupPassword(
            @RequestParam("token") String token,
            @Valid @RequestBody SetupPasswordRequestDTO req) {

        emailVerificationService.setupPassword(token, req);
        return ApiResponse.success(HttpStatus.OK.value(), "Password set successfully. You can now log in.", null);
    }


//    @PostMapping("/oauth-login")
//    public ResponseEntity<?> oauthLogin(@Valid @RequestBody OAuthLoginRequest req) {
//        var response = oAuthService.handleOAuthLogin(req);
//        return ApiResponse.success("OAuth login successful", response);
//    }
//
//    @PostMapping("/oauth-register")
//    public ResponseEntity<?> oauthRegister(@Valid @RequestBody OAuthRegisterRequest req) {
//        var response = oAuthService.handleOAuthRegister(req);
//        return ApiResponse.success("OAuth register successful", response);
//    }

//    @PostMapping("/test-email")
//    public ResponseEntity<?> testEmail(@RequestParam String email) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(email);
//            helper.setSubject("Test Email from Rockstock");
//            helper.setText("<h1>Hello from Rockstock!</h1>", true);
//            helper.setFrom("rockstockfe@gmail.com");
//
//            mailSender.send(message);
//            return ResponseEntity.ok("Email sent successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error: " + e.getMessage());
//        }
//    }
}
