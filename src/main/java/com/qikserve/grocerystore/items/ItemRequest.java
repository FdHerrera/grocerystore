package com.qikserve.grocerystore.items;

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
public class ItemRequest {
    private Integer price;
    private Integer quantity;
    private String description;
    private String name;
}
