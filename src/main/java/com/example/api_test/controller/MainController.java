package com.example.api_test.controller;


import com.example.api_test.entity.File_Entity;
import com.example.api_test.entity.User_info;
import com.example.api_test.jwt_config.JwtUtil;
import com.example.api_test.repo.FileRepository;
import com.example.api_test.repo.UserRepository;
import com.example.api_test.service.AuthService;
import com.example.api_test.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private FileService fileService;

   @Autowired
    private FileRepository filerepo;

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

    @GetMapping("/files")
    public ResponseEntity<List<File_Entity>> getUserFiles(Authentication authentication) {
        try {
            String username = authentication.getName();
            System.out.println("Fetching files for user: " + username);

            // Fetch user's files from service
            List<File_Entity> files = fileService.getUserFiles(username);

            // Print each file to console
            for (File_Entity file : files) {
                System.out.println("File ID: " + file.getId());
                System.out.println("File Name: " + file.getFileName());
                System.out.println("File Type: " + file.getFileType());
                System.out.println("File Size: " + file.getFileSize());
                System.out.println("Upload Date: " + file.getUploade_date());
                System.out.println("Downloads: " + file.getDownloads());
                System.out.println("-----------------------------");
            }

            return ResponseEntity.ok(files);
        } catch (Exception e) {
            e.printStackTrace(); // print full stack trace
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/cs/upload")
    public ResponseEntity<File_Entity> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String username = authentication.getName(); // from JWT
            File_Entity savedFile = fileService.uploadFile(file, username);
            return ResponseEntity.ok(savedFile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/files/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();

            int user_id = userRepository.findIdByUser_Username(username);

            System.out.println("User requesting file: " + username+"User Id  "+ user_id);
            // Fetch file by id
            File_Entity file = filerepo.findByIdAndUserId(id, user_id);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Debug log file info
            System.out.println("Downloading File:");
            System.out.println("File ID: " + file.getId());
            System.out.println("File Name: " + file.getFileName());
            System.out.println("File Type: " + file.getFileType());
            System.out.println("File Size: " + file.getFileSize());
            System.out.println("Upload Date: " + file.getUploade_date());
            System.out.println("Downloads: " + file.getDownloads());
            System.out.println("-----------------------------");

            // Increase download count
//            file.setDownloads(file.getDownloads() + 1);
//            fileService.saveFile(file);

            // Return file as downloadable response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .body(file.getData());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable("id") Long id, Authentication authentication) {

        String username = authentication.getName();

        int userId = userRepository.findIdByUser_Username(username);

        System.out.println("Delete print  "+ id + "USer ID "+ userId);
        boolean deleted = fileService.deleteFileByUser(id, userId);

        if (deleted) {
            return ResponseEntity.ok().body("File deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found or unauthorized");
        }
    }


}