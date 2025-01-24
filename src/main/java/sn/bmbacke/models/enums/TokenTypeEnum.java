package sn.bmbacke.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenTypeEnum {
    LOGIN_OTP("LOGIN_OTP"),
    SIGNUP_OTP("SIGNUP_OTP"),
    PASSWORD_RESET("PASSWORD_RESET");

    private final String value;
}
