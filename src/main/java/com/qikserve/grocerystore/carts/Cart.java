package com.qikserve.grocerystore.carts;

import com.qikserve.grocerystore.discounts.Discount;
import com.qikserve.grocerystore.items.Item;
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
public class Cart {
    @NotNull
    private Integer total;
    private List<Discount> discounts;
    @NotNull
    private List<Item> items;
    @NotNull
    private String id;
}
