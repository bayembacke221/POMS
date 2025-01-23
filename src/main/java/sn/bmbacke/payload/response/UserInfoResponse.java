package sn.bmbacke.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class UserInfoResponse {
    private String username;
    private String email;
    private Set<String> roles;
    private List<String> groups;
}