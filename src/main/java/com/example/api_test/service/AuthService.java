package com.example.api_test.service;

import com.example.api_test.entity.User_info;
import com.example.api_test.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+<>?";



    public static String user_verification_code_genaretor(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character of each type
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        String ALLS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
        // Fill remaining characters
        for (int i = password.length(); i < length; i++) {
            password.append(ALLS.charAt(random.nextInt(ALLS.length())));
        }

        // Shuffle characters
        char[] pwdArray = password.toString().toCharArray();
        for (int i = 0; i < pwdArray.length; i++) {
            int j = random.nextInt(pwdArray.length);
            char temp = pwdArray[i];
            pwdArray[i] = pwdArray[j];
            pwdArray[j] = temp;
        }

        return new String(pwdArray);
    }
    public User_info register(User_info user) {
        // validate exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null) user.setRoles("ROLE_USER");
        return userRepository.save(user);
    }
    public boolean verifyUser(Integer id) {
        int rows = userRepository.updateOtpTrueByFileId(id);
        return rows > 0;
    }

    public boolean set_verification_code(Integer id,  String code) {
        int rows = userRepository.findbyotp(id,code);
        return rows > 0;
    }

    public boolean delete_user_otp( int userId) {
        int updatedRows = userRepository.deleteOtpByUserId_OTP(userId);
        return updatedRows > 0;
    }

    public User_info login(String username, String rawPassword) {
        Optional<User_info> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new RuntimeException("Invalid credentials");
        User_info user = userOpt.get();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }
}
