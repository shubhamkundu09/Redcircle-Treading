package com.redcircle.service;

import com.redcircle.domain.VERIFICATION_TYPE;
import com.redcircle.modal.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public User findUserByJwt(String jwt);
    public User findUserByEmail(String email);
    public User findUserByUserId(Long id);

    public User enableTwoFactorAuthentication(VERIFICATION_TYPE verificationType,
                                              String sendTo,
                                              User user);


    User updatePassword(User user, String newPassword);

}
