package com.redcircle.controller;

import com.redcircle.modal.User;
import com.redcircle.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user){

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

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
