package com.example.api_test.service;

import org.springframework.stereotype.Service;

import java.util.Base64;


@Service
public class Encryption_L {

    public String encode(String input) {
        return Base64.getUrlEncoder().encodeToString(input.getBytes());
    }

    public String decode(String input) {
        return new String(Base64.getUrlDecoder().decode(input));
    }
}

