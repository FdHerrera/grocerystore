package com.qikserve.grocerystore.items;

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
public class Item {
    @NotNull
    private BigDecimal price;
    @NotNull
    private Integer quantity;
    private String description;
    @NotNull
    private String id;
    @NotNull
    private String name;
}
