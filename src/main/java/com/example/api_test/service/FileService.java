package com.example.api_test.service;

import com.example.api_test.entity.File_Entity;
import com.example.api_test.entity.User_info;
import com.example.api_test.repo.FileRepository;
import com.example.api_test.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private final String DIGITS = "0123456789";
    private final String SPECIAL_CHARS = "!@#$%^&*()-_=+<>?";
    private long file_id;

//    public long getFile_id() {
//        return file_id;
//    }
//
//    public void setFile_id(long file_id) {
//        this.file_id = file_id;
//    }

    private final String ALL = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }
    @Transactional(readOnly = true)
    public List<File_Entity> getUserFiles(String username) {
        User_info user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return fileRepository.findByUser(user);
    }
    public File_Entity uploadFile(MultipartFile file, String username) throws IOException {
        User_info user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        File_Entity fileEntity = new File_Entity();
        String originalFilename = file.getOriginalFilename();
        String decodedFilename = URLDecoder.decode(originalFilename, StandardCharsets.UTF_8);
        fileEntity.setFileName(decodedFilename);
        fileEntity.setFileType(file.getContentType());
       // fileEntity.setFileotp(generateOtp());
        fileEntity.setFileSize((int) file.getSize());
        fileEntity.setData(file.getBytes());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = LocalDateTime.now().format(formatter);
        fileEntity.setUploade_date(formattedDate);
        fileEntity.setDownloads(0);
        fileEntity.setUser(user);

        return fileRepository.save(fileEntity);
    }

    public String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character of each type
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Fill remaining characters
        for (int i = password.length(); i < length; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
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
    public void startTimer(int seconds, long fileId, int userId ,boolean timer_status ) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        System.out.println("Print file id stop here "+ fileId);
//        setFile_id(fileId);
        final int[] remaining = {seconds};

        System.out.println("Timer started for " + seconds + " seconds...");

            ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
                if (remaining[0] > 0) {
                    System.out.println("Time left: " + remaining[0] + " seconds");
                  //  System.out.println("File otp null or not  "+fileRepository.isFileOtpNull(getFile_id()));
                    if (!timer_status) {
                        if (delete_otp(fileId, userId)) {
                            System.out.println("Timer forced stop! OTP deleted.");
                        }
                        System.out.println("timer forced to be stop here ");
                    }
                    if (fileRepository.isFileOtpNull(fileId)){
                        System.out.println("timer should be stop here ");
                        scheduler.shutdown();
                    }
                    remaining[0]--;
                } else {
                    // Timer finished, delete OTP
                    if (delete_otp(fileId, userId)) {
                        System.out.println("Timer finished! OTP deleted.");
                    }
                    scheduler.shutdown();
                }
            }, 0, 1, TimeUnit.SECONDS);



        System.out.println("Timer stopped");

    }



    public boolean setOtp(Long fileId, int userId, String otp) {
        int updatedRows = fileRepository.updateOtpByFileIdAndUserId(otp, fileId, userId);
        return updatedRows > 0;
    }

    public boolean delete_otp(Long fileId, int userId) {
        int updatedRows = fileRepository.deleteOtpByFileIdAndUserId(fileId, userId);
        return updatedRows > 0;
    }

    public boolean deleteFileByUser(Long fileId, int userId) {
        int deletedRows = fileRepository.deleteByIdAndUserId(fileId, userId);
        return deletedRows > 0;
    }
}
