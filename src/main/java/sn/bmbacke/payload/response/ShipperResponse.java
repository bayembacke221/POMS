package sn.bmbacke.payload.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipperResponse {
    private Integer id;
    private String companyName;
    private String phone;
}
