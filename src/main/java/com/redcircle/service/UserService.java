package com.redcircle.service;

import com.redcircle.modal.User;

public interface UserService {

    public User findUserByJwt(String jwt);
    public User findUserByEmail(String email);
    public User findUserByUserId(Long id);

    public User enableTwoFactorAuthentication(User user);

    User updatePassword(User user, String newPassword);

}
