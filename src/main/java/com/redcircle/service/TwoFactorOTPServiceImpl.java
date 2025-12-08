package com.redcircle.service;

import com.redcircle.modal.TwoFactorOTP;
import com.redcircle.modal.User;
import com.redcircle.repo.TwoFactorOTPRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

public class TwoFactorOTPServiceImpl implements TwoFactorOTPService{

    @Autowired
    private TwoFactorOTPRepo twoFactorOTPRepo;


    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
        twoFactorOTP.setId(id);
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setJwt(jwt);
        twoFactorOTP.setUser(user);
        return twoFactorOTPRepo.save(twoFactorOTP);
    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {

        return twoFactorOTPRepo.findByUserId(userId);
    }

    @Override
    public TwoFactorOTP findById(String id) {
       Optional<TwoFactorOTP> op =  twoFactorOTPRepo.findById(id);
       if (op.isEmpty()){
           throw new RuntimeException("OTP Not Found...................");
       }
        return op.get();
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp) {
        return twoFactorOTP.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP) {
        twoFactorOTPRepo.delete(twoFactorOTP);

    }
}
