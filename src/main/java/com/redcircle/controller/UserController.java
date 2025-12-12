package com.redcircle.controller;

import com.redcircle.domain.VERIFICATION_TYPE;
import com.redcircle.modal.User;
import com.redcircle.modal.VerificationCode;
import com.redcircle.service.EmailService;
import com.redcircle.service.UserService;
import com.redcircle.service.VerificationCodeService;
import jakarta.mail.MessagingException;
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

   @Autowired
   private VerificationCodeService verificationCodeService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @PatchMapping("/verification/{verificationType}/sendotp")
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt,
                                                    @PathVariable VERIFICATION_TYPE verificationType) throws MessagingException {
        User user = userService.findUserByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUserId(user.getId());
        if (verificationCode!=null){
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);

        }

        if (verificationType.equals(VERIFICATION_TYPE.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }
        return new ResponseEntity<>("OTP Successfully Sent", HttpStatus.OK);

    }


    @PatchMapping("/enable-2fa/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt,
                                                              @PathVariable String otp){
        User user = userService.findUserByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUserId(user.getId());
        String sendTo = verificationCode.getVerificationType().equals(VERIFICATION_TYPE.EMAIL)?
                verificationCode.getEmail() : verificationCode.getPhone();

        boolean isVerified = verificationCode.getOtp().equals(otp);
        if (isVerified){
            User updatedUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(),
                    sendTo, user);

            verificationCodeService.deleteVerificationCodeById(verificationCode);
            return new ResponseEntity<>(updatedUser,HttpStatus.OK);
        }
        throw new RuntimeException("Otp is Wrong");

    }



}
