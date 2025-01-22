package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        String id,
        @NotEmpty(message = "Company name is required")
        @NotNull(message = "Company name is required")
        String companyName,
        String contactName,
        String contactTitle,
        String address,
        String city,
        String region,
        String postalCode,
        String country,
        String phone,
        String fax
) {
}
