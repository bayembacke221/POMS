package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ShipperRequest (
    Integer id,
    @NotEmpty(message = "Le nom de la compagnie est obligatoire")
    @NotNull(message = "Le nom de la compagnie ne peut pas être vide")
    String companyName,
    String phone
){
}
