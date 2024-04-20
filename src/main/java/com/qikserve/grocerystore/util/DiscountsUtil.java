package com.qikserve.grocerystore.util;

import com.qikserve.grocerystore.discounts.Discount;
import com.qikserve.grocerystore.items.Item;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiscountsUtil {


    public static Integer calcTotal(final List<Discount> discounts, final List<Item> items) {
        if (items.isEmpty()) {
            return 0;
        }
        Integer totalWithNoDiscounts = items.stream()
                .map(Item::getPrice)
                .reduce(0, Integer::sum);
        Integer staticDiscount = discounts.stream()
                .map(Discount::getStaticAmount)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum);
        int totalWithStaticDiscounts = totalWithNoDiscounts - staticDiscount;
        if (totalWithStaticDiscounts < 0) {
            return 0;
        }
        Double percentageDiscounts = discounts.stream()
                .map(Discount::getPercentage)
                .filter(Objects::nonNull)
                .reduce(0.0, Double::sum);
        double percentageDiscount = Math.min(percentageDiscounts, 100.0);
        if (percentageDiscount == 100.0) {
            return 0;
        }
        if (percentageDiscount == 0.0) {
            return totalWithStaticDiscounts;
        }
        double total = totalWithStaticDiscounts * percentageDiscount / 100.0;
        return (int) total;
    }

}