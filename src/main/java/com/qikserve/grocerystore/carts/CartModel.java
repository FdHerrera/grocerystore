package com.qikserve.grocerystore.carts;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class CartModel {
    @NotNull
    private Integer total;
    @NotNull
    private List<String> discounts;
    @NotNull
    private List<String> items;
    @NotNull
    private String id;
}
