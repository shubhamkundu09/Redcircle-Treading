package com.redcircle.controller;

import com.redcircle.config.JwtProvider;
import com.redcircle.modal.User;
import com.redcircle.repo.UserRepo;
import com.redcircle.response.AuthResponse;
import com.redcircle.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CustomUserDetailService customUserDetailService;

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
    public ResponseEntity<AuthResponse> signin(@RequestBody User user){
        
        String username = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(username,password);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        if (user.getTwoFactorAuth().isEnabled()){

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


}
