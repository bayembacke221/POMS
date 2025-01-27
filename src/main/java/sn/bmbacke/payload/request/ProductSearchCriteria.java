package sn.bmbacke.payload.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductSearchCriteria {
    private String name;
    private Integer categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean discontinued;
    private Boolean lowStock;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
