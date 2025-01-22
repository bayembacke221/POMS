package sn.bmbacke.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionResponse {
    private Integer id;
    private String regionDescription;
}
