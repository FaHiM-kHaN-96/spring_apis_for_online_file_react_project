package com.example.api_test.controller;

import com.example.api_test.repo.UserRepository;
import com.example.api_test.service.AuthService;
import com.example.api_test.service.EmailService;
import com.example.api_test.service.Encryption_L;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Public_Controller {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    @Autowired
    private Encryption_L encryption;


    @RequestMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }

    @GetMapping("/verification/{code}")
    public String verification(@PathVariable ("code")  String code) {

        String email = userRepository.findusernamebyotp(encryption.decode(code));

        System.out.println(email);
        //System.out.printf("otp   " +encryption.decode(code));
        int userid = userRepository.findIdByUser_Username(email);
        if (userRepository.existsByUsername(email)&& authService.verifyUser(userid)) {
            if (authService.delete_user_otp(userid)) {
                return "redirect:https://unmanacled-shela-fathomlessly.ngrok-free.dev/FileManager";
            }


        }

        return "redirect:https://unmanacled-shela-fathomlessly.ngrok-free.dev/VerificationStatus";
    }
}
