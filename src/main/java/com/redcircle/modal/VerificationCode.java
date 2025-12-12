package com.redcircle.modal;


import com.redcircle.domain.VERIFICATION_TYPE;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String otp;

    @OneToOne
    private User user;

    private String email;

    private String phone;

    private VERIFICATION_TYPE verificationType;

}

