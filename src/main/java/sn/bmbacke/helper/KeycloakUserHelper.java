package sn.bmbacke.helper;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sn.bmbacke.exception.ApiException;
import sn.bmbacke.models.User;
import sn.bmbacke.payload.dto.AuthChangePwdDto;
import sn.bmbacke.payload.request.RefreshTokenRequest;
import sn.bmbacke.repository.UserRepository;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserHelper {

    @Value("${app.keycloak.serverUrl}")
    private String authServerUrl;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.admin.clientId}")
    private String clientId;

    @Value("${app.keycloak.admin.clientSecret}")
    private String clientSecret;

    private final UserRepository userRepository;

    public AccessTokenResponse authenticate(String username, String password) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(username)
                    .password(password)
                    .build();

            return keycloak.tokenManager().getAccessToken();
        } catch (Exception e) {
            log.error("Authentication failed for user {}", username, e);
            throw new ApiException("Authentication failed");
        }
    }

    public void updatePassword(String userId, String login, AuthChangePwdDto pwdDto) {
        Keycloak keycloak = getAdminClient();
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(pwdDto.getNewPassword());
        credential.setTemporary(false);

        try {
            keycloak.realm(realm).users().get(userId).resetPassword(credential);
        } catch (Exception e) {
            log.error("Failed to update password for user {}", login, e);
            throw new ApiException("Failed to update password");
        }
    }

    public void logoutAllSessions(String userId) {
        try {
            Keycloak keycloak = getAdminClient();
            keycloak.realm(realm).users().get(userId).logout();
        } catch (Exception e) {
            log.error("Failed to logout all sessions for user {}", userId, e);
            throw new ApiException("Failed to logout sessions");
        }
    }

    private Keycloak getAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    public void updatePasswordWithoutCheck(String kcId, String newPassword) {
        Keycloak keycloak = getAdminClient();
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);

        try {
            keycloak.realm(realm).users().get(kcId).resetPassword(credential);
        } catch (Exception e) {
            throw new ApiException("Failed to update password");
        }
    }

    public void refreshTokenOp(RefreshTokenRequest request) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", request.getRefreshToken());
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);

            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", authServerUrl, realm);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    formData,
                    AccessTokenResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ApiException("Failed to refresh token");
            }
        } catch (Exception e) {
            throw new ApiException("Failed to refresh token");
        }
    }

    public void logout() {
        if (!isAuthenticated()) {
            throw new ApiException("Not authenticated");
        }
        String sessionId = getCurrentSession();
        invalidateSession(sessionId);
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated();
    }

    private String getCurrentSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getCredentials() instanceof Jwt) {
            return ((Jwt) auth.getCredentials()).getId();
        }
        throw new ApiException("Invalid session");
    }

    private void invalidateSession(String sessionId) {
        try {
            getAdminClient().realm(realm).deleteSession(sessionId, false);
        } catch (Exception e) {
            throw new ApiException("Failed to invalidate session");
        }
    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException("Not authenticated");
        }

        String username = authentication.getName();
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }
    public String createUser(UserRepresentation userRepresentation) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm("master")  // Use master realm for admin
                    .clientId("admin-cli")
                    .username("admin") // Your admin username
                    .password("admin") // Your admin password
                    .build();

            Response response = keycloak.realm(realm).users().create(userRepresentation);

            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                log.info("Created user with ID: {}", userId);
                return userId;
            } else {
                throw new ApiException("Failed to create user in Keycloak: " + response.getStatusInfo().getReasonPhrase());
            }
        } catch (Exception e) {
            log.error("Error creating user in Keycloak", e);
            throw new ApiException("Failed to create user");
        }
    }


    public void enableUser(String userId) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm("master")
                    .clientId("admin-cli")
                    .username("admin")  // Admin credentials
                    .password("admin")
                    .build();

            UserResource userResource = keycloak.realm(realm).users().get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(true);
            userResource.update(user);

            log.info("User {} enabled successfully", userId);
        } catch (Exception e) {
            log.error("Failed to enable user {}", userId, e);
            throw new ApiException("Failed to enable user");
        }
    }


}
