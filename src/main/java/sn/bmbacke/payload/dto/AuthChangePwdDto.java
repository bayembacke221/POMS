package sn.bmbacke.payload.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthChangePwdDto {
    private String oldPassword;
    private String newPassword;
    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String confirmPassword;
}
