package com.klu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String mailUsername;

    public EmailService(
        JavaMailSender mailSender,
        @Value("${spring.mail.username:}") String mailUsername
    ) {
        this.mailSender = mailSender;
        this.mailUsername = mailUsername;
    }

    public void sendEmail(String to, String subject, String body) {
        validateMailConfiguration();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new IllegalStateException("Email delivery failed. Check your Gmail SMTP configuration and app password.", ex);
        }
    }

    private void validateMailConfiguration() {
        if (mailUsername == null || mailUsername.isBlank() || mailUsername.contains("change-me")) {
            throw new IllegalStateException("Email delivery is not configured. Set MAIL_USERNAME to your Gmail address.");
        }
    }
}
