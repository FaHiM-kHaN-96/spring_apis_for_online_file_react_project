package com.example.api_test.controller;


import com.example.api_test.entity.File_Entity;
import com.example.api_test.entity.User_info;
import com.example.api_test.helper.ServeFile_Helper;
import com.example.api_test.jwt_config.JwtUtil;
import com.example.api_test.repo.FileRepository;
import com.example.api_test.repo.UserRepository;
import com.example.api_test.service.AuthService;
import com.example.api_test.service.EmailService;
import com.example.api_test.service.Encryption_L;
import com.example.api_test.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.1.183:3000"}, allowCredentials = "true")
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

   @Autowired
   private Encryption_L encryptionL;


   @Autowired
   private EmailService emailService;




    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String fullname = body.get("fullname");

        System.out.println("Check signup full name   "+ fullname);
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



            if (!userRepository.check_verification(user.getId())) {
                System.out.println("Verification email sent successfully to user: " + username);
                boolean mailSent = emaiilSender(user.getId());
                if (mailSent ){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of(
                                    "success", false,
                                    "message", "Account not verified. Verification email sent successfully!"
                            ));
                    }


            }else {
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "token", token,
                        "username", user.getUsername()
                ));
            }
            return ResponseEntity.status(401).body(Map.of("message", "Login failed: " + "Internal error"));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(Map.of("message", "Login failed: " + ex.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        System.out.printf("authHeader:  ", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Missing token"));
        }

        String token = authHeader.substring(7);
        System.out.println("token: " + token);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
        }

        String username = jwtUtil.extractUsername(token);
        int user_id = userRepository.findIdByUser_Username(username);
        System.out.printf("print userid  "+user_id);

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
    public ResponseEntity<?> getUserFiles(Authentication authentication) {
        try {
            String username = authentication.getName();
            int user_id = userRepository.findIdByUser_Username(username);
            System.out.println("removed local variable " +user_id);
            //userRepository.check_verification(getUserid());
            if (userRepository.check_verification(user_id)) {
                System.out.println("Fetching files for user: " + username);

                // setUserid(null);
                if (user_id == 0 && username == null) {
                    System.out.printf("Userid is null for username ");
                }
                System.out.printf("Fetching files userID: " + user_id);

                // Fetch user's files from service
                List<File_Entity> files = fileService.getUserFiles(username);
                List<ServeFile_Helper> specific_file_details = new ArrayList<>();

                // Print each file to console
                for (File_Entity file : files) {
                    ServeFile_Helper helper = new ServeFile_Helper();

                    System.out.println("File ID: " + file.getId());
                    helper.setId(file.getId());
                    System.out.println("File Name: " + file.getFileName());
                    helper.setFileName(file.getFileName());
                    System.out.println("File Type: " + file.getFileType());
                    helper.setFileType(file.getFileType());
                    System.out.println("File Size: " + file.getFileSize());
                    helper.setFileSize(file.getFileSize());
                    System.out.println("Upload Date: " + file.getUploade_date());
                    helper.setUploade_date(file.getUploade_date());
                    System.out.println("Downloads: " + file.getDownloads());
                    helper.setDownloads(file.getDownloads());
                    System.out.println("-----------------------------");
                    helper.setUser(file.getUser());
                    specific_file_details.add(helper);

                }

                return ResponseEntity.ok(specific_file_details);
            } else {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "success", false,
                                "message", "Account not verified and email sending failed!"
                        ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "An unexpected error occurred: " + e.getMessage()
                    ));
        }
    }



    private boolean emaiilSender(int id) {
        try {
            // ✅ নতুন OTP তৈরি
            String otp = encryptionL.encode(authService.user_verification_code_genaretor(200));
            System.out.println("Generated verification code: " + otp);

            // ✅ ডিকোড করা OTP চেক
            String decodedOtp = encryptionL.decode(otp);

            // ✅ যদি একই OTP ডাটাবেজে না থাকে
            if (userRepository.isOtpAvailable(decodedOtp)) {

                // ✅ OTP সেট করা হয়েছে কিনা চেক
                if (authService.set_verification_code(id, decodedOtp)) {


                    String link = "https://ce5e636080a6.ngrok-free.app/verification/" + otp;


                    boolean emailSent = emailService.sendVerificationEmail(id, link);

                    if (emailSent) {
                        System.out.println("Verification code not found & email sent successfully!");
                        return true;
                    } else {
                        System.err.println("Email sending failed!");
                        return false;
                    }
                } else {
                    System.err.println("Failed to set verification code for user ID: " + id);
                    return false;
                }

            } else {
                System.out.println("Verification code already exists in database!");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error in emailSender: " + e.getMessage());
            return false; // ❌ কোনো exception ঘটলে false
        }
    }


    @GetMapping("/share/{fileId}/download")
    public ResponseEntity<String> shareLink(@PathVariable("fileId") long fileId, Authentication authentication) {

        System.out.println("share link triggerd");
        try {
            String username = authentication.getName();

            System.out.println("Generate share link for userID: " + " | fileId: " + fileId);
            int user_id = userRepository.findIdByUser_Username(username);
            System.out.println("removed local variable " +user_id);
            String password =  fileService.generatePassword(100);
            System.out.println("password  " + password);
            String shareLink=null;
            String encrypt_pass = encryptionL.encode(password);
            System.out.println("encode pass "+ encrypt_pass);
            boolean password_exist = filerepo.existsByfileotp(password);

            if (fileService.setOtp(fileId,user_id,password) && !password_exist){
                System.out.println("Saved password  "+ password);
                try {
                    Thread.sleep(2000); // 2000 milliseconds = 2 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                }
                fileService.startTimer(120,fileId,user_id,true);
                shareLink = "https://ce5e636080a6.ngrok-free.app/share_file/" + encrypt_pass  ;
                return ResponseEntity.ok(shareLink);

            }else {
                return ResponseEntity.badRequest().body("Invalid link please try again");
            }

          //  https://unmanacled-shela-fathomlessly.ngrok-free.dev/

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error while generating share link");
        }
    }




  @PostMapping("/stop/{fileid}")
    public ResponseEntity<String> stopAfterTimer(
            @PathVariable("fileid") long fileid,
            Authentication authentication) {

        try {
            // Get authenticated user info
            String username = authentication.getName();
            int user_id = userRepository.findIdByUser_Username(username);
            System.out.println("removed local variable " +user_id);
            System.out.println("Stop timer  "+username);
           fileService.startTimer(120,fileid,user_id,false);


                return ResponseEntity.ok("✅ Link action started successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error while starting link action");
        }
    }

    @PostMapping("/start/{fileid}")
    public ResponseEntity<String> startAfterTimerd(
            @PathVariable("fileid") long fileid,
            Authentication authentication) {


        System.out.println("Start timer triggered ");
        try {
            // Get authenticated user info
            String username = authentication.getName();
            System.out.println("Stop timer  "+username);
            int user_id = userRepository.findIdByUser_Username(username);
            System.out.println("removed local variable " +user_id);

           fileService.startTimer(120,fileid,user_id,true);
            return ResponseEntity.ok("✅ Link action started successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error while starting link action");
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