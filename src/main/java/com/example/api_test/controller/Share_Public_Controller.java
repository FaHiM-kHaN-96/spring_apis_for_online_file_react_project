package com.example.api_test.controller;

import com.example.api_test.entity.File_Entity;
import com.example.api_test.jwt_config.JwtUtil;
import com.example.api_test.repo.FileRepository;
import com.example.api_test.repo.UserRepository;
import com.example.api_test.service.AuthService;
import com.example.api_test.service.Encryption_L;
import com.example.api_test.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/share_file")
//@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.1.183:3000"}, allowCredentials = "true")
public class Share_Public_Controller {

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

    private int user_id;
    private long fileid;

    public long getFileid() {
        return fileid;
    }

    public void setFileid(long fileid) {
        this.fileid = fileid;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @GetMapping("/{otp_pass}")
    public ResponseEntity<byte[]> sharedownloadFile(@PathVariable("otp_pass") String otp_pass,HttpHeaders headers) {
        try {


          ///  int user_id = getUserid();

            String decode_pass = encryptionL.decode(otp_pass);
            System.out.println("Faced password  "+ decode_pass);

            if (!filerepo.existsByfileotp(decode_pass)) {
                System.out.println("OTP deleted.");
//                return ResponseEntity.status(HttpStatus.GONE) // 410 Gone
//                        .body("OTP already used".getBytes());
            }
            // System.out.println("Share requesting file: " + username+"User Id  "+ user_id);

            File_Entity file = filerepo.findByFileOtp(decode_pass);
            if (file == null){
                return ResponseEntity.status(HttpStatus.GONE) // 410 Gone
                        .body("Link in not valid".getBytes());
            }
            int user_id = file.getUser().getId() ;
            setUser_id(user_id);
            System.out.println("Print UserID " +user_id);
            if (file == null) {
                System.out.println("File is null share");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if(file.getFileotp()!= null && decode_pass.equals(file.getFileotp())){

                // Debug log file info
                System.out.println("Share downloading File:");
                System.out.println("File ID: " + file.getId());
                setFileid(file.getId());
                System.out.println("File Name: " + file.getFileName());
                System.out.println("File Type: " + file.getFileType());
                System.out.println("File Size: " + file.getFileSize());
                System.out.println("Upload Date: " + file.getUploade_date());
                System.out.println("Downloads: " + file.getDownloads());
                System.out.println("-----------------------------");

                // Increase download count
                filerepo.incrementFilecountByFileIdAndUserId(file.getId(),user_id);

                if (fileService.delete_otp(file.getId(), user_id)) {

                    headers.add("Location", "/share_file/stop/timer");

                    System.out.println("Link used  OTP deleted.");

                }
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                        .contentType(MediaType.parseMediaType(file.getFileType()))
                        .body(file.getData());


            }


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @GetMapping("/stop/timer")
    public ResponseEntity<String> stopAfterTimer() {

        System.out.println("Use this method stop/timer");

        try {

           fileService.startTimer(0,getFileid(),getUser_id(),false);

            return ResponseEntity.ok("✅ File Downloader successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error while starting link action");
        }
    }
}
