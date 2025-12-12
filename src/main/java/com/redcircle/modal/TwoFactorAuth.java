package com.redcircle.modal;

import com.redcircle.domain.VERIFICATION_TYPE;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnabled = false;

    private VERIFICATION_TYPE sendTo;

}
