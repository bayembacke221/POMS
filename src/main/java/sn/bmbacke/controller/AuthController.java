package sn.bmbacke.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import sn.bmbacke.payload.response.UserInfoResponse;
import sn.bmbacke.service.SecurityService;

import java.util.Set;

@RestController
@RequestMapping("auth")
@Slf4j
public class AuthController {
    private final SecurityService securityService;

    public AuthController(SecurityService securityService) {
        this.securityService = securityService;
    }

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
}
