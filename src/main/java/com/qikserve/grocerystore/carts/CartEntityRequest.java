package com.qikserve.grocerystore.carts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@NoArgsConstructor
public class CartEntityRequest {
    private Integer total;
    private List<String> discountIds;
    private List<String> itemIds;
}