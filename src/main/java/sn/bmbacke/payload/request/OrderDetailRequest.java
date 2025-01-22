package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderDetailRequest(
        @NotNull(message = "L'identifiant de la commande est obligatoire")
    Integer orderId,
        @NotNull(message = "L'identifiant du produit est obligatoire")
    Integer productId,
        @NotNull(message = "Le prix unitaire est obligatoire")
        @Positive(message = "Le prix unitaire doit être positif")
    Double unitPrice,
        @NotNull(message = "La quantité est obligatoire")
        @Positive(message = "La quantité doit être positive")
    Short quantity,
        @NotNull(message = "La remise est obligatoire")
    Float discount
) {
}
