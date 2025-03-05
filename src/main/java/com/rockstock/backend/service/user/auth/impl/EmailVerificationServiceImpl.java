package com.rockstock.backend.service.user.auth.impl;

import com.rockstock.backend.entity.user.EmailVerificationToken;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.user.auth.dto.SetupPasswordRequestDTO;
import com.rockstock.backend.infrastructure.user.repository.EmailVerificationTokenRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.service.user.auth.EmailVerificationService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${frontend.url}")
    private String frontendUrl;

    public EmailVerificationServiceImpl(PasswordEncoder passwordEncoder, JavaMailSender mailSender,
                                        UserRepository userRepository,
                                        EmailVerificationTokenRepository tokenRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void sendVerificationEmail(String email) {
        // Cari user berdasarkan email
        User user = userRepository.findByEmailContainsIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<EmailVerificationToken> existingToken = this.tokenRepository.findByUser_IdAndExpiryDateLessThan(user.getId(),
                LocalDateTime.now());
        EmailVerificationToken verificationToken;


        if (existingToken.isEmpty()) {
            verificationToken = new EmailVerificationToken(user);
            tokenRepository.save(verificationToken);

        }
        else {
            verificationToken = existingToken.get();
        }

        String verificationLink = frontendUrl + "/auth/verify-email?token=" + verificationToken.getToken();


        String subject = "Rockstock Email Verification";
        String content = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; }
                        .container { max-width: 600px; margin: auto; padding: 20px; }
                        .button { background-color: #e74c3c; color: #fff !important; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h2>Welcome to Rockstock!</h2>
                        <p>Thank you for registering. Please verify your email address by clicking the button below:</p>
                        <a href="%s" class="button">Verify Email</a>
                        <p>If you did not sign up, please ignore this email.</p>
                        <p>Best,<br>Rockstock Team</p>
                    </div>
                </body>
                </html>
                """.formatted(verificationLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom(fromEmail);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email", e);
        }
    }


    @Override
    @Transactional
    public void verifyEmail(String token) {
        System.out.println("Verifying token: " + token);
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
        System.out.println("Token found: " + verificationToken);

        // Check if token has expired
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        // Mark user as verified
        User user = verificationToken.getUser();
        user.setIsVerified(true);
        userRepository.save(user);

    }


    @Override
    public void resendVerificationEmail(String email) {
//
//        User user = userRepository.findByEmailContainsIgnoreCase(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//
//        if (user.getIsVerified()) {
//            throw new RuntimeException("Email is already verified");
//        }
//
//
//        tokenRepository.deleteByUser(user);
//
//
//        EmailVerificationToken newToken = new EmailVerificationToken(user);
//        tokenRepository.save(newToken);
//
//
//        sendVerificationEmail(email);
    }



    @Override
    @Transactional
    public void setupPassword(String token, SetupPasswordRequestDTO req) {
        // 1. Cari token di database
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        // 2. Ambil user terkait dengan token
        User user = verificationToken.getUser();

        // 3. Hash password baru
        String hashedPassword = passwordEncoder.encode(req.getPassword());

        // 4. Simpan password baru ke database
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // 5. Hapus token setelah digunakan
        tokenRepository.delete(verificationToken);
    }
}
