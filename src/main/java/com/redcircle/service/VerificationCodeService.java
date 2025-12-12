package com.redcircle.service;

import com.redcircle.domain.VERIFICATION_TYPE;
import com.redcircle.modal.User;
import com.redcircle.modal.VerificationCode;
import org.springframework.stereotype.Service;

@Service
public interface VerificationCodeService {

    VerificationCode sendVerificationCode(User user, VERIFICATION_TYPE verificationType);

    VerificationCode getVerificationCodeById(Long id);

    VerificationCode getVerificationCodeByUserId(Long id);

    void deleteVerificationCodeById(VerificationCode verificationCode);
}
