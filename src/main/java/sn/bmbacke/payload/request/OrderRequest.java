package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotNull;

public record OrderRequest(
    Integer id,
    @NotNull(message = "Le client est obligatoire")
    Integer customerID,
    @NotNull(message = "L'employé est obligatoire")
    Integer employeeID,
    String orderDate,
    String requiredDate,
    String shippedDate,
    Integer shipVia,
    String freight,
    String shipName,
    String shipAddress,
    String shipCity,
    String shipRegion,
    String shipPostalCode,
    String shipCountry
) {
}
