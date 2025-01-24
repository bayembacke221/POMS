package sn.bmbacke.payload.request;

import lombok.Data;

@Data
public class LoginOTPRequest {
    private Long userId;
    private String username;
    private String password;
    private String otp;
}