
package com.arun.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Product name is required")
        @Size(max = 150, message = "Name cannot exceed 150 characters")
        String name,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock quantity cannot be negative")
        Integer stockQuantity

) {
}
