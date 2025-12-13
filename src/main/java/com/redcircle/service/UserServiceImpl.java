package com.redcircle.service;

import com.redcircle.config.JwtProvider;
import com.redcircle.domain.VERIFICATION_TYPE;
import com.redcircle.modal.TwoFactorAuth;
import com.redcircle.modal.User;
import com.redcircle.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;




    @Override
    public User findUserByJwt(String jwt) {
        String email = JwtProvider.getEmailFromJwtToken(jwt);
        User user =  userRepo.findByEmail(email);
        if (user== null){
            throw new BadCredentialsException("User Not Found.............");
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        User user =  userRepo.findByEmail(email);
        if (user== null){
            throw new BadCredentialsException("User Not Found...............");
        }
        return user;
    }

    @Override
    public User findUserByUserId(Long id) {
        Optional<User> ou =  userRepo.findById(id);
        if (ou.isEmpty()){
            throw new BadCredentialsException("User Not Found....................");
        }
        return ou.get();

    }

    @Override
    public User enableTwoFactorAuthentication(VERIFICATION_TYPE verificationType, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);

        user.setTwoFactorAuth(twoFactorAuth);
        return userRepo.save(user);
    }



    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepo.save(user);
    }
}





