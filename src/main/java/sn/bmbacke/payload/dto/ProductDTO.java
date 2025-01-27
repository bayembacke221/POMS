package sn.bmbacke.payload.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductDTO {
    private Integer id;
    private String name;
    private Integer categoryId;
    private String categoryName;
    private String quantityPerUnit;
    private BigDecimal unitPrice;
    private Integer unitsInStock;
    private Integer unitsOnOrder;
    private Integer reorderLevel;
    private boolean discontinued;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
