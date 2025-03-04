package com.rockstock.backend.service.user.impl;

import com.rockstock.backend.entity.user.PasswordResetToken;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.user.dto.ConfirmResetPasswordDTO;
import com.rockstock.backend.infrastructure.user.dto.ResetPasswordRequestDTO;
import com.rockstock.backend.infrastructure.user.repository.PasswordResetTokenRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.service.user.ResetPasswordService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${frontend.url}")
    private String frontendUrl;

    public ResetPasswordServiceImpl(PasswordEncoder passwordEncoder, JavaMailSender mailSender,
                                    UserRepository userRepository, PasswordResetTokenRepository tokenRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void sendResetPasswordEmail(ResetPasswordRequestDTO request) {
        User user = userRepository.findByEmailContainsIgnoreCase(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        tokenRepository.deleteByUser_Id(user.getId()); // DELETE token sebelumnya

        PasswordResetToken resetToken = new PasswordResetToken(user);
        tokenRepository.save(resetToken); // Simpan token baru

        String resetLink = frontendUrl + "/dashboard/user/profile/reset-password/confirm-reset-password?token=" + resetToken.getToken();
        String subject = "Reset Your Password - Rockstock";
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
                <h2>Reset Your Password</h2>
                <p>We received a request to reset your password. Click the button below to proceed:</p>
                <a href="%s" class="button">Reset Password</a>
                <p>If you did not request this, please ignore this email.</p>
                <p>Best,<br>Rockstock Team</p>
            </div>
        </body>
        </html>
        """.formatted(resetLink);

        sendEmail(user.getEmail(), subject, content);
    }

    @Override
    @Transactional
    public void confirmResetPassword(ConfirmResetPasswordDTO request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getResetToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }


    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom(fromEmail);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email", e);
        }
    }
}
