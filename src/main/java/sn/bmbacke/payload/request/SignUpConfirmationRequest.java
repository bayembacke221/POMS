package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpConfirmationRequest {
    @NotBlank
    private String login;

    @NotBlank
    private String otp;
}
