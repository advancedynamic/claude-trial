package com.example.secureapp.service;

import com.example.secureapp.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendPasswordResetEmail(User user, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Password Reset Request");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "You have requested to reset your password. Please click the link below to reset your password:\n\n" +
                "%s/auth/reset-password?token=%s\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Secure App Team",
                user.getFullName(), baseUrl, token
            ));

            mailSender.send(message);
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getEmail(), e);
        }
    }

    public void sendMagicLinkEmail(User user, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Magic Link Login");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "You have requested to login via magic link. Please click the link below to login:\n\n" +
                "%s/auth/magic-link?token=%s\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Secure App Team",
                user.getFullName(), baseUrl, token
            ));

            mailSender.send(message);
            log.info("Magic link email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send magic link email to: {}", user.getEmail(), e);
        }
    }

    public void sendWelcomeEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Welcome to Secure App");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Welcome to Secure App! Your account has been created successfully.\n\n" +
                "You can now login at: %s/auth/login\n\n" +
                "Best regards,\n" +
                "Secure App Team",
                user.getFullName(), baseUrl
            ));

            mailSender.send(message);
            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", user.getEmail(), e);
        }
    }
}
