package sn.bmbacke.service;
import lombok.*;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import org.thymeleaf.context.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import sn.bmbacke.exception.ApiException;
import sn.bmbacke.helper.KeycloakUserHelper;
import sn.bmbacke.helper.app.OTPGenerator;
import sn.bmbacke.models.PasswordResetToken;
import sn.bmbacke.models.User;
import sn.bmbacke.models.enums.TokenTypeEnum;
import sn.bmbacke.payload.dto.AuthChangePwdDto;
import sn.bmbacke.payload.dto.AuthDto;
import sn.bmbacke.payload.dto.UserPasswordResetDTO;
import sn.bmbacke.payload.mapper.AuthMapper;
import sn.bmbacke.payload.request.*;
import sn.bmbacke.repository.PasswordResetTokenRepository;
import sn.bmbacke.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final KeycloakUserHelper kcUserHelper;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthMapper authMapper;
    private final OTPGenerator otpGenerator;

    public AuthDto signIn(LoginRequest loginRequest) {
        AccessTokenResponse tokenResponse = kcUserHelper.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        User user = userRepository.findByLogin(loginRequest.getUsername())
                .orElseThrow(() -> new ApiException("User not found"));
        return authMapper.mapToAuthDto(user, tokenResponse);
    }
    public void resendSignUpOTP(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new ApiException("User not found"));

        // Invalider les anciens OTP
        List<PasswordResetToken> oldTokens = tokenRepository
                .findByUserIdAndTokenType(user.getId(), TokenTypeEnum.SIGNUP_OTP);
        oldTokens.forEach(token -> {
            token.setValid(false);
            tokenRepository.save(token);
        });

        // Générer nouveau OTP
        String otp = otpGenerator.generateOTP();
        PasswordResetToken newToken = new PasswordResetToken();
        newToken.setUser(user);
        newToken.setToken(otp);
        newToken.setTokenType(TokenTypeEnum.SIGNUP_OTP);
        newToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        tokenRepository.save(newToken);

        // Envoyer email avec nouveau OTP
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("otp", otp);
        emailService.sendSignupConfirmationEmail(user.getEmail(), context);
    }
    public AuthDto signInOTP(LoginOTPRequest request) {
        List<PasswordResetToken> tokens = tokenRepository.findByTokenAndTokenTypeAndUserId(
                request.getOtp(), TokenTypeEnum.LOGIN_OTP, request.getUserId());

        if (tokens.isEmpty() || tokens.stream().noneMatch(this::isValidToken)) {
            throw new ApiException("Invalid OTP");
        }

        AccessTokenResponse tokenResponse = kcUserHelper.authenticate(request.getUsername(), request.getPassword());
        invalidateTokens(tokens);
        return authMapper.mapToAuthDto(userRepository.findByLogin(request.getUsername()).get(), tokenResponse);
    }

    public void changePassword(AuthChangePwdDto request) {
        User user = kcUserHelper.getCurrentUser();
        kcUserHelper.updatePassword(user.getKcId(), user.getLogin(), request);
        // Invalider toutes les sessions existantes
        kcUserHelper.logoutAllSessions(user.getKcId());
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        String token = kcUserHelper.generateResetToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setTokenType(TokenTypeEnum.PASSWORD_RESET);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        tokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(UserPasswordResetDTO resetRequest) {
        PasswordResetToken token = tokenRepository.findByToken(resetRequest.getToken())
                .orElseThrow(() -> new ApiException("Invalid token"));

        if (!isValidToken(token)) {
            throw new ApiException("Token expired");
        }

        User user = token.getUser();
        kcUserHelper.updatePasswordWithoutCheck(user.getKcId(), resetRequest.getNewPassword());

        token.setValid(false);
        tokenRepository.save(token);
    }

    private boolean isValidToken(PasswordResetToken token) {
        return token.isValid() && token.getExpiryDate().isAfter(LocalDateTime.now());
    }

    private void invalidateTokens(List<PasswordResetToken> tokens) {
        tokens.forEach(token -> token.setValid(false));
        tokenRepository.saveAll(tokens);
    }


    public AuthDto signUp(SignUpRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new ApiException("User already exists");
        }

        try {
            // Créer l'utilisateur dans Keycloak
            UserRepresentation keycloakUser = new UserRepresentation();
            keycloakUser.setUsername(request.getLogin());
            keycloakUser.setEmail(request.getEmail());
            keycloakUser.setFirstName(request.getFirstName());
            keycloakUser.setLastName(request.getLastName());
            keycloakUser.setEnabled(true);

            // Définir les credentials
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            keycloakUser.setCredentials(Arrays.asList(credential));

            // Ajouter l'utilisateur à Keycloak
            String kcId = kcUserHelper.createUser(keycloakUser);

            // Créer l'utilisateur en base de données
            User user = User.builder()
                    .kcId(kcId)
                    .login(request.getLogin())
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .enabled(true)
                    .signInOtpReq(1) // Activer OTP par défaut
                    .locOtpNotifChannels("email,sms")
                    .activate(1)
                    .build();

            user = userRepository.save(user);

            // Générer et sauvegarder OTP
            String otp = otpGenerator.generateOTP();
            PasswordResetToken otpToken = new PasswordResetToken();
            otpToken.setUser(user);
            otpToken.setToken(otp);
            otpToken.setTokenType(TokenTypeEnum.SIGNUP_OTP);
            otpToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
            tokenRepository.save(otpToken);

            // Envoyer email de confirmation avec OTP
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("otp", otp);
            emailService.sendSignupConfirmationEmail(user.getEmail(), context);

            // Authentifier l'utilisateur et retourner les tokens
            AccessTokenResponse tokenResponse = kcUserHelper.authenticate(
                    request.getLogin(),
                    request.getPassword()
            );

            return AuthDto.builder()
                    .id(user.getId())
                    .login(user.getLogin())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .accessToken(tokenResponse.getToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .build();

        } catch (Exception e) {
            log.error("Error during user signup", e);
            throw new ApiException("Signup failed");
        }
    }

    public AuthDto confirmSignUp(SignUpConfirmationRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new ApiException("User not found"));

        PasswordResetToken otpToken = tokenRepository
                .findByTokenAndTokenTypeAndUserId(
                        request.getOtp(),
                        TokenTypeEnum.SIGNUP_OTP,
                        user.getId())
                .stream()
                .filter(token -> token.isValid() && token.getExpiryDate().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(() -> new ApiException("Invalid or expired OTP"));

        // Invalider le token OTP
        otpToken.setValid(false);
        tokenRepository.save(otpToken);

        // Activer l'utilisateur dans Keycloak si nécessaire
        kcUserHelper.enableUser(user.getKcId());

        return AuthDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }


}
