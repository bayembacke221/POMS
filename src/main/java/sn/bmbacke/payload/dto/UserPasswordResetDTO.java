package sn.bmbacke.payload.dto;

import lombok.Data;

@Data
public class UserPasswordResetDTO {
    private String token;
    private String newPassword;
    private String confirmPassword;
}

