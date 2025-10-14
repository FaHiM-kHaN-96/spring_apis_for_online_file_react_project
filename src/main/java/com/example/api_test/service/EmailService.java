package com.example.api_test.service;

import com.example.api_test.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;


    public boolean sendVerificationEmail(int id, String link) {
        String to = userRepository.findUsernameById(id);
        System.out.println("email: " + to);
        System.out.println("Sending verification email to " + to + "   " + link);

        // যদি ইউজার ইমেইল না পাওয়া যায়
        if (to == null || to.isEmpty()) {
            System.out.println("User email not found for id: " + id);
            return false;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("🔐 Verify Your Email Address – OnlineFile");

        String emailBody = """
            Dear User,

            Thank you for joining OnlineFile! 🌐

            To complete your registration and secure your account, please verify your email by clicking the link below:

            🔗 Verification Link:
            %s

            (If the link doesn’t work, please copy and paste it into your browser.)

            This verification helps us keep your account safe and ensure it’s really you.

            If you didn’t request this email, please ignore it — no further action is required.

            Warm regards,
            The OnlineFile Support Team
            support-onlinefile@chatsky.online
            """.formatted(link);

        message.setText(emailBody);
        message.setFrom("support-onlinefile@chatsky.online");

        try {
            mailSender.send(message);
            System.out.println("Verification email sent to: " + to);
            return true; // ✅ সফলভাবে পাঠানো হয়েছে
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
            return false; // ❌ পাঠানো ব্যর্থ
        }
    }



}