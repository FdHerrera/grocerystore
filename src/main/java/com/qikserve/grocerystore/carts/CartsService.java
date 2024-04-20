package com.qikserve.grocerystore.carts;


import com.qikserve.grocerystore.config.EntityPaths;
import com.qikserve.grocerystore.discounts.Discount;
import com.qikserve.grocerystore.discounts.DiscountsService;
import com.qikserve.grocerystore.items.Item;
import com.qikserve.grocerystore.items.ItemsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.qikserve.grocerystore.util.DiscountsUtil.calcTotal;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

@AllArgsConstructor
@Service
public class CartsService {
    private final DiscountsService discountsService;
    private final EntityPaths entityPaths;
    private final ItemsService itemsService;
    private final RestTemplate restTemplate;

    public Cart getCart(final String id) {
        CartModel entityResponse = restTemplate.getForObject(
                entityPaths.getCarts().concat("/").concat(id),
                CartModel.class
        );
        if (isNull(entityResponse)) {
            throw new NoSuchElementException();
        }
        List<Item> items = entityResponse.getItems().stream()
                .map(itemsService::getItem)
                .toList();
        List<Discount> discounts = entityResponse.getDiscounts().stream()
                .map(discountsService::getDiscount)
                .toList();
        return Cart.builder()
                .id(entityResponse.getId())
                .total(entityResponse.getTotal())
                .items(items)
                .discounts(discounts)
                .build();
    }

    public String createCart(final CartRequest cartRequest) {
        validateIds(cartRequest);
        List<Item> items = Optional.ofNullable(cartRequest.getItemIds())
                .orElse(emptyList())
                .stream()
                .map(itemsService::getItem)
                .toList();
        List<Discount> discounts = Optional.ofNullable(cartRequest.getDiscountIds())
                .orElse(emptyList())
                .stream()
                .map(discountsService::getDiscount)
                .toList();
        CartEntityRequest cartEntityRequest = CartEntityRequest.builder()
                .discountIds(cartRequest.getDiscountIds())
                .itemIds(cartRequest.getItemIds())
                .total(calcTotal(discounts, items))
                .build();
        CartModel created = restTemplate.postForObject(
                entityPaths.getCarts(),
                cartEntityRequest,
                CartModel.class
        );
        return created.getId();
    }

    public Cart updateCart(final String id, final CartRequest cartRequest) {
        if (isNull(getCart(id))) {
            throw new NoSuchElementException();
        }
        validateIds(cartRequest);
        List<Item> items = Optional.ofNullable(cartRequest.getItemIds())
                .orElse(emptyList())
                .stream()
                .map(itemsService::getItem)
                .toList();
        List<Discount> discounts = Optional.ofNullable(cartRequest.getDiscountIds())
                .orElse(emptyList())
                .stream()
                .map(discountsService::getDiscount)
                .toList();
        CartEntityRequest cartEntityRequest = CartEntityRequest.builder()
                .discountIds(cartRequest.getDiscountIds())
                .itemIds(cartRequest.getItemIds())
                .total(calcTotal(discounts, items))
                .build();
        restTemplate.put(
                entityPaths.getCarts().concat("/").concat(id),
                cartEntityRequest
        );
        return getCart(id);
    }

    private void validateIds(final CartRequest cartRequest) {
        List<String> itemIds = Optional.ofNullable(cartRequest.getItemIds()).orElse(emptyList());
        itemIds.forEach(itemId -> {
            Item item = itemsService.getItem(itemId);
            if (isNull(item)) {
                throw new NoSuchElementException("Item not found %s".formatted(itemId));
            }
        });
        List<String> discountIds = Optional.ofNullable(cartRequest.getDiscountIds()).orElse(emptyList());
        discountIds.forEach(discountId -> {
            Discount discount = discountsService.getDiscount(discountId);
            if (isNull(discount)) {
                throw new NoSuchElementException("Discount not found %s".formatted(discountId));
            }
        });
    }
}
