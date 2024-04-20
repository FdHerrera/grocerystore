package com.qikserve.grocerystore.items;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@NoArgsConstructor
public class ItemRequest {
    private BigDecimal price;
    private Integer quantity;
    private String description;
    private String name;
}
