package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegionRequest(
    Integer id,
    @NotNull(message = "La description de la région est obligatoire")
    @NotEmpty(message = "La description de la région ne peut pas être vide")
    String regionDescription
) {
}
