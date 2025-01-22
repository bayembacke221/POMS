package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EmployeeRequest(
    Integer id,
    @NotNull(message = "Le nom est obligatoire")
    @NotEmpty(message = "Le nom ne peut pas être vide")
    String lastName,
    @NotNull(message = "Le prénom est obligatoire")
    @NotEmpty(message = "Le prénom ne peut pas être vide")
    String firstName,
    String title,
    String titleOfCourtesy,
    String birthDate,
    String hireDate,
    String address,
    String city,
    String region,
    String postalCode,
    String country,
    String homePhone,
    String extension
) {
}
