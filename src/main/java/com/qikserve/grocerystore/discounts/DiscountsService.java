package com.qikserve.grocerystore.discounts;

import com.qikserve.grocerystore.config.EntityPaths;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Objects.isNull;

@AllArgsConstructor
@Service
public class DiscountsService {
    private final EntityPaths entityPaths;
    private final RestTemplate restTemplate;

    public Discount getDiscount(final String id) {
        return restTemplate.getForObject(
                entityPaths.getDiscounts().concat("/").concat(id),
                Discount.class
        );
    }

    public String createDiscount(final DiscountRequest discountRequest) {
        Discount createdDiscount = restTemplate.postForObject(
                entityPaths.getDiscounts(),
                discountRequest,
                Discount.class
        );
        return Optional.ofNullable(createdDiscount)
                .map(Discount::getId)
                .orElseThrow();
    }

    public Discount updateDiscount(final String id, final DiscountRequest discountRequest) {
        if (isNull(getDiscount(id))) {
            throw new NoSuchElementException();
        }
        restTemplate.put(
                entityPaths.getDiscounts().concat("/").concat(id),
                discountRequest
        );
        return getDiscount(id);
    }
}
