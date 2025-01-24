package sn.bmbacke.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import sn.bmbacke.helper.KeycloakUserHelper;
import sn.bmbacke.models.User;
import sn.bmbacke.payload.dto.AuthChangePwdDto;
import sn.bmbacke.payload.dto.AuthDto;
import sn.bmbacke.payload.dto.UserPasswordResetDTO;
import sn.bmbacke.payload.dto.ViewTokenDto;
import sn.bmbacke.payload.request.*;
import sn.bmbacke.payload.response.ApiResponseDto;
import sn.bmbacke.payload.response.UserInfoResponse;
import sn.bmbacke.service.AuthService;
import sn.bmbacke.service.SecurityService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final SecurityService securityService;
    private final KeycloakUserHelper keycloakUserHelper;
    private final AuthService authService;

    @GetMapping("/user-info")
    public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        log.info("User info: {}", jwt.getClaims());
        UserInfoResponse userInfo = UserInfoResponse.builder()
                .username(jwt.getClaimAsString("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .roles(securityService.getCurrentUserRoles())
                .groups(jwt.getClaimAsStringList("groups"))
                .build();
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/roles")
    public ResponseEntity<Set<String>> getCurrentUserRoles() {
        return ResponseEntity.ok(securityService.getCurrentUserRoles());
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Authentification utilisateur")
    public ResponseEntity<AuthDto> signIn(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.signIn(loginRequest));
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Inscription utilisateur")
    @ApiResponse(responseCode = "201", description = "Compte créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<AuthDto> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.signUp(request));
    }

    @PostMapping("/sign-up/confirm")
    @Operation(summary = "Confirmation inscription par OTP")
    @ApiResponse(responseCode = "200", description = "Compte confirmé")
    @ApiResponse(responseCode = "400", description = "OTP invalide")
    public ResponseEntity<AuthDto> confirmSignUp(@Valid @RequestBody SignUpConfirmationRequest request) {
        return ResponseEntity.ok(authService.confirmSignUp(request));
    }

    @PostMapping("/sign-up/resend-otp")
    @Operation(summary = "Renvoyer code OTP")
    public ResponseEntity<ApiResponseDto> resendOTP(@RequestParam String login) {
        authService.resendSignUpOTP(login);
        return ResponseEntity.ok(new ApiResponseDto("OTP renvoyé"));
    }

    @PostMapping("/sign-in/otp-confirmation")
    @Operation(summary = "Confirmation OTP")
    public ResponseEntity<AuthDto> signInOTP(@Valid @RequestBody LoginOTPRequest loginRequest) {
        return ResponseEntity.ok(authService.signInOTP(
                loginRequest
        ));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Rafraîchir le token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        keycloakUserHelper.refreshTokenOp(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Obtenir profil utilisateur courant")
    public ResponseEntity<User> getMe() {
        return ResponseEntity.ok(keycloakUserHelper.getCurrentUser());
    }

    @PutMapping("/me/change-password")
    @Operation(summary = "Changer mot de passe")
    public ResponseEntity<?> updatePasswordMe(@Valid @RequestBody AuthChangePwdDto request) {
        authService.changePassword(request);
        return ResponseEntity.ok(new ApiResponseDto("Mot de passe mis à jour"));
    }

    @PostMapping("/forgotten-password")
    @Operation(summary = "Demander réinitialisation mot de passe")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody AuthDto request) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(new ApiResponseDto("Email envoyé"));
    }

    @PostMapping("/forgotten-password/validate")
    @Operation(summary = "Valider nouveau mot de passe")
    public ResponseEntity<?> resetPasswordValidate(@Valid @RequestBody UserPasswordResetDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponseDto("Mot de passe mis à jour"));
    }

    @GetMapping("/sign-out")
    @Operation(summary = "Déconnexion")
    public ResponseEntity<ApiResponseDto> signOut() {
        keycloakUserHelper.logout();
        return ResponseEntity.ok(new ApiResponseDto("Déconnecté"));
    }

}
