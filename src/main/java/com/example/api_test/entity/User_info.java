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
    @OneToMany(cascade = CascadeType.ALL)
    private List<File_Entity> fIleModels = new ArrayList<>();

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fullname='" + fullname + '\'' +
                ", roles='" + roles + '\'' +
                ", fIleModels=" + fIleModels +
                '}';
    }

    public List<File_Entity> getfIleModels() {
        return fIleModels;
    }

    public void setfIleModels(List<File_Entity> fIleModels) {
        this.fIleModels = fIleModels;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
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
}
