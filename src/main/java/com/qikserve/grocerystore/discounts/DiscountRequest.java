package com.qikserve.grocerystore.discounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@NoArgsConstructor
public class DiscountRequest {
    private String description;
    private Double percentage;
    private Integer staticAmount;
}
