package com.example.api_test.service;

import com.example.api_test.repo.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;


    public void sendVerificationEmail(int id, String link) {

        String to = userRepository.findUsernameById(id);
        System.out.printf("email " +to);
        System.out.println("Sending verification email to " + to +"   " +link);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification");
        message.setText("Dear User,\n\nPlease use the following code to verify your email: " + link + "\n\nThank you!");
        message.setFrom("support-onlinefile@chatsky.online"); // Must match spring.mail.username

        try {
            mailSender.send(message);

            System.out.println("Verification email sent to: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

}