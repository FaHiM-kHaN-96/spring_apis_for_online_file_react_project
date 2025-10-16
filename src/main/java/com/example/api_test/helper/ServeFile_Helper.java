package com.example.api_test.helper;

import com.example.api_test.entity.User_info;


public class ServeFile_Helper {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileotp;
    private int fileSize;
    private String uploade_date;
    private int downloads;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileotp() {
        return fileotp;
    }

    public void setFileotp(String fileotp) {
        this.fileotp = fileotp;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getUploade_date() {
        return uploade_date;
    }

    public void setUploade_date(String uploade_date) {
        this.uploade_date = uploade_date;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public User_info getUser() {
        return user;
    }

    public void setUser(User_info user) {
        this.user = user;
    }

    private User_info user;

}
