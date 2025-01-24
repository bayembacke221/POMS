package sn.bmbacke.payload.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class AuthDto {
    private Long id;
    private String login;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Set<String> roles;
    private Set<String> permissions;
    private String accessToken;
    private String refreshToken;
}
