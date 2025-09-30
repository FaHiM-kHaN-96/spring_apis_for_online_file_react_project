package com.example.api_test.service;

import com.example.api_test.entity.File_Entity;
import com.example.api_test.entity.User_info;
import com.example.api_test.repo.FileRepository;
import com.example.api_test.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

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
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFileType(file.getContentType());
       // fileEntity.setFileotp(generateOtp());
        fileEntity.setFileSize((int) file.getSize());
        fileEntity.setData(file.getBytes());
        fileEntity.setUploade_date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        fileEntity.setDownloads(0);
        fileEntity.setUser(user);

        return fileRepository.save(fileEntity);
    }
}
