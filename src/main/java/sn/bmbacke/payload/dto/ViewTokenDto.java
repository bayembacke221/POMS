package sn.bmbacke.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.keycloak.representations.AccessTokenResponse;

@Data
@AllArgsConstructor
public class ViewTokenDto {
    private AccessTokenResponse tokens;
}
