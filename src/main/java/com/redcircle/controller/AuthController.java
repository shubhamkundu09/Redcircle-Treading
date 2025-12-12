package com.redcircle.controller;

import com.redcircle.config.JwtProvider;
import com.redcircle.modal.TwoFactorOTP;
import com.redcircle.modal.User;
import com.redcircle.repo.UserRepo;
import com.redcircle.response.AuthResponse;
import com.redcircle.service.CustomUserDetailService;
import com.redcircle.service.EmailService;
import com.redcircle.service.TwoFactorOTPServiceImpl;
import com.redcircle.utils.OtpUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private TwoFactorOTPServiceImpl twoFactorOTPService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody User user){

        User isExist = userRepo.findByEmail(user.getEmail());
        if (isExist!=null){
            throw new RuntimeException("Email is Already Used with Another Account.................");
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setFullName(user.getFullName());

        User savedUser = userRepo.save(newUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt= JwtProvider.generateToken(auth);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("Token Generated");
        res.setStatus(true);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }



    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody User user) throws MessagingException {
        
        String username = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(username,password);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        User user1 = userRepo.findByEmail(username);


        if (user.getTwoFactorAuth().isEnabled()){
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Two Factor Authentication is Enabled");
            authResponse.setTwoFactorAuthEnable(true);
            String otp = OtpUtils.generateOTP();

//          check if previous otp exist or not
            TwoFactorOTP checkOtp = twoFactorOTPService.findByUser(user1.getId());

//          here we are deleting the previous otp if exist
            if (checkOtp!=null){
                twoFactorOTPService.deleteTwoFactorOtp(checkOtp);
            }

//            here we are creating a new twofactor otp
            TwoFactorOTP twoFactorOTP = twoFactorOTPService.createTwoFactorOtp(user1,otp,jwt);

//            here we are sending email
            emailService.sendVerificationOtpEmail(user1.getEmail(), otp);

            authResponse.setSession(twoFactorOTP.getId());
            return new ResponseEntity<>(authResponse,HttpStatus.OK);


        }

        AuthResponse res = new AuthResponse();
        res.setStatus(true);
        res.setJwt(jwt);
        res.setMessage("Login Success");

        return new ResponseEntity<>(res,HttpStatus.OK);

    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
        if (userDetails==null){
            throw new BadCredentialsException("Username is Invalid...............");
        }

        if (!password.equals(userDetails.getPassword())){
            throw new BadCredentialsException("Password is Wrong....................");
        }

       return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }


    @PostMapping("/2fa/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigninOtp(@PathVariable String otp,
                                                        @RequestParam String id) throws Exception {
        TwoFactorOTP twoFactorOTP = twoFactorOTPService.findById(id);
        if (twoFactorOTPService.verifyTwoFactorOtp(twoFactorOTP,otp)){
            AuthResponse authResponse = new AuthResponse();
            authResponse.setStatus(true);
            authResponse.setMessage("Login Success");
            authResponse.setTwoFactorAuthEnable(true);
            authResponse.setJwt(twoFactorOTP.getJwt());

            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }
        throw new Exception("Invalid OTP");
    }


}
