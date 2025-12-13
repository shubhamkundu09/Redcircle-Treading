package com.redcircle.service;

import com.redcircle.domain.VERIFICATION_TYPE;
import com.redcircle.modal.User;
import com.redcircle.modal.VerificationCode;
import com.redcircle.repo.VerificationCodeRepo;
import com.redcircle.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService{

    @Autowired
    private VerificationCodeRepo verificationCodeRepo;


    @Override
    public VerificationCode sendVerificationCode(User user, VERIFICATION_TYPE verificationType) {
        VerificationCode verificationCode1 = new VerificationCode();
        verificationCode1.setOtp(OtpUtils.generateOTP());
        verificationCode1.setVerificationType(verificationType);
        return verificationCodeRepo.save(verificationCode1);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) {
        Optional<VerificationCode> ovc = verificationCodeRepo.findById(id);
        if(ovc.isPresent()){
            return ovc.get();
        }
        throw new RuntimeException("Verification Code Not Found..................");
    }

    @Override
    public VerificationCode getVerificationCodeByUserId(Long id) {
        return verificationCodeRepo.findByUserId(id);
    }

    @Override
    public void deleteVerificationCodeById(VerificationCode verificationCode) {

        verificationCodeRepo.delete(verificationCode);

    }


}






