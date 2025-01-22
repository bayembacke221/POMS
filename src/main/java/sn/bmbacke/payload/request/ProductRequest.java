package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequest(
    Integer id,
    @NotNull(message = "Le nom du produit est obligatoire")
    @NotEmpty(message = "Le nom du produit ne peut pas être vide")
    String productName,
    @NotNull(message = "Le fournisseur est obligatoire")
    Integer supplierID,
    @NotNull(message = "La catégorie est obligatoire")
    Integer categoryID,
    String quantityPerUnit,
    @Positive(message = "Le prix unitaire doit être positif")
    Double unitPrice,
    Short unitsInStock,
    Short unitsOnOrder,
    Short reorderLevel,
    Boolean discontinued
) {
}
