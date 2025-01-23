package sn.bmbacke.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        try {
            // Log JWT claims for debugging
            log.debug("JWT Claims: {}", jwt.getClaims());

            // Extract roles
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                authorities.addAll(roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList()));
            }

            // Extract groups
            List<String> groups = jwt.getClaimAsStringList("groups");
            if (groups != null) {
                authorities.addAll(groups.stream()
                        .map(group -> new SimpleGrantedAuthority("GROUP_" + group))
                        .collect(Collectors.toList()));
            }

            log.debug("Extracted authorities: {}", authorities);

        } catch (Exception e) {
            log.error("Error extracting claims from JWT", e);
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}