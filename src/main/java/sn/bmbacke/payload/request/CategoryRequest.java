package sn.bmbacke.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        Integer id,
        @NotNull(message = "Category name is required")
        @NotEmpty(message = "Category name is required")
        String categoryName,
        String description
) {
}
