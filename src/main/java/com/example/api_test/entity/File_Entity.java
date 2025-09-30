package com.example.api_test.entity;

import jakarta.persistence.*;

import java.util.Arrays;

@Entity
public class File_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;

    private String fileotp;
    private int fileSize;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;

    private String uploade_date;

    // Optional fields for tracking downloads
    private int downloads;

    @ManyToOne
    private User_info user;

    @Override
    public String toString() {
        return "FIleModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileotp='" + fileotp + '\'' +
                ", fileSize=" + fileSize +
                ", data=" + Arrays.toString(data) +
                ", uploade_date='" + uploade_date + '\'' +
                ", downloads=" + downloads +
                ", user=" + user +
                '}';
    }

    public String getUploade_date() {
        return uploade_date;
    }

    public void setUploade_date(String uploade_date) {
        this.uploade_date = uploade_date;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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
}
