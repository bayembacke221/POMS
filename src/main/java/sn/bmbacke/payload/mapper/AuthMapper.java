package sn.bmbacke.payload.mapper;

import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Component;
import sn.bmbacke.models.User;
import sn.bmbacke.payload.dto.AuthDto;

@Component
public class AuthMapper {
    public AuthDto mapToAuthDto(User user, AccessTokenResponse tokenResponse) {
        return AuthDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getKGroups())
                .accessToken(tokenResponse.getToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .build();
    }
}
