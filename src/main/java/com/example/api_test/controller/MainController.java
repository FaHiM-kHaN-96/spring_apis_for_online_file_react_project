package com.example.api_test.controller;


import com.example.api_test.entity.User_info;
import com.example.api_test.jwt_config.JwtUtil;
import com.example.api_test.repo.UserRepository;
import com.example.api_test.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.1.183:3000"}, allowCredentials = "true")
public class MainController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String fullname = body.get("fullname");
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) return ResponseEntity.badRequest().body(Map.of("message", "Missing fields"));

        if (userRepository.existsByUsername(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        }

        User_info user = new User_info();
        user.setFullname(fullname);
        user.setUsername(email);
        user.setPassword(password);
        user.setRoles("ROLE_USER");

        authService.register(user);

        return ResponseEntity.ok(Map.of("message", "Signup successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        try {
            User_info user = authService.login(username, password);
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", user.getRoles());

            String token = jwtUtil.generateToken(user.getUsername(), claims);

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "username", user.getUsername()
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(Map.of("message", "Login failed: " + ex.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Missing token"));
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
        }

        String username = jwtUtil.extractUsername(token);
        User_info user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "User not found"));
        }

        // ✅ Use HashMap to allow null values safely
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("fullname", user.getFullname()); // safe even if null

        return ResponseEntity.ok(response);
    }

}