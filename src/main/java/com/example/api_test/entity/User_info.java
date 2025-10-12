package com.example.api_test.entity;


import jakarta.persistence.*;


import java.util.ArrayList;

import java.util.List;
@Entity


public class User_info {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String fullname;
    private String roles;
    private String one_time_password;
    private boolean verification_status;
    @OneToMany(cascade = CascadeType.ALL)
    private List<File_Entity> fIleModels = new ArrayList<>();

    @Override
    public String toString() {
        return "User_info{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fullname='" + fullname + '\'' +
                ", roles='" + roles + '\'' +
                ", one_time_password='" + one_time_password + '\'' +
                ", verification_status=" + verification_status +
                ", fIleModels=" + fIleModels +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getOne_time_password() {
        return one_time_password;
    }

    public void setOne_time_password(String one_time_password) {
        this.one_time_password = one_time_password;
    }

    public boolean isVerification_status() {
        return verification_status;
    }

    public void setVerification_status(boolean verification_status) {
        this.verification_status = verification_status;
    }

    public List<File_Entity> getfIleModels() {
        return fIleModels;
    }

    public void setfIleModels(List<File_Entity> fIleModels) {
        this.fIleModels = fIleModels;
    }
}
