package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SupplierRequest(
    Integer id,
    @NotNull(message = "Le nom de la compagnie est obligatoire")
    @NotEmpty(message = "Le nom de la compagnie ne peut pas être vide")
    String companyName,
    String contactName,
    String contactTitle,
    String address,
    String city,
    String region,
    String postalCode,
    String country,
    String phone,
    String fax,
    String homePage
) {
}
