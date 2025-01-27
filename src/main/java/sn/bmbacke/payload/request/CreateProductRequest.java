package sn.bmbacke.payload.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(max = 40, message = "Product name must not exceed 40 characters")
    private String name;

    @NotNull(message = "Category is required")
    private Integer categoryId;

    @Size(max = 20, message = "Quantity per unit must not exceed 20 characters")
    private String quantityPerUnit;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @Min(value = 0, message = "Units in stock must not be negative")
    private Integer unitsInStock;

    @Min(value = 0, message = "Units on order must not be negative")
    private Integer unitsOnOrder;

    @Min(value = 0, message = "Reorder level must not be negative")
    private Integer reorderLevel;
}

