package com.example.api_test.service;

import com.example.api_test.entity.User_info;
import com.example.api_test.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User_info register(User_info user) {
        // validate exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null) user.setRoles("ROLE_USER");
        return userRepository.save(user);
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
