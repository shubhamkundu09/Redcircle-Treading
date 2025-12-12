package com.redcircle.controller;

import com.redcircle.domain.VERIFICATION_TYPE;
import com.redcircle.modal.User;
import com.redcircle.service.EmailService;
import com.redcircle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @PatchMapping("/verification/{verificationType}/sendotp")
    public ResponseEntity<User> sendVerificationOtp(@RequestHeader("Authorization") String jwt,
                                                    @PathVariable VERIFICATION_TYPE verificationType){
        User user = userService.findUserByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }


    @PatchMapping("/enable-2fa/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }



}
