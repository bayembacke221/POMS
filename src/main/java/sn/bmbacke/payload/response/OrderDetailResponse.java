package sn.bmbacke.payload.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Double unitPrice;
    private Short quantity;
    private Float discount;
}
