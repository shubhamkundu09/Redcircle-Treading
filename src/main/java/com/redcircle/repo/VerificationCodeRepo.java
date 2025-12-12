package com.redcircle.repo;

import com.redcircle.modal.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepo extends JpaRepository<VerificationCode, Long> {

    public VerificationCode findByUserId(Long userId);
}
