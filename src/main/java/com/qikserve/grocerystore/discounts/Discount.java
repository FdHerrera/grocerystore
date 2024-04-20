package com.qikserve.grocerystore.discounts;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class Discount {
    @NotNull
    private String id;
    private String description;
    private Double percentage;
    private BigDecimal staticAmount;
}
