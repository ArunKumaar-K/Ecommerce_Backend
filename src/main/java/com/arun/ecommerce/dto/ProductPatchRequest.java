
package com.arun.ecommerce.dto;

import java.math.BigDecimal;

public record ProductPatchRequest(
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity
) {
}
