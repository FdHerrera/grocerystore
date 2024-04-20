package com.qikserve.grocerystore.util;

import com.qikserve.grocerystore.discounts.Discount;
import com.qikserve.grocerystore.items.Item;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class DiscountsUtilTest {

    @Test
    void shouldReturnZeroWhenNoItems() {
        int actual = DiscountsUtil.calcTotal(
                List.of(Discount.builder().staticAmount(Integer.MAX_VALUE).build()),
                emptyList()
        );

        assertThat(actual).isZero();
    }

    @Test
    void shouldReturnZeroWhenStaticDiscountsIsGreaterThanTotalPriceOfItems() {
        int actual = DiscountsUtil.calcTotal(
                List.of(Discount.builder().staticAmount(Integer.MAX_VALUE).build()),
                List.of(Item.builder().price(Integer.MAX_VALUE - 1).build())
        );

        assertThat(actual).isZero();
    }

    @Test
    void shouldReturnHalfWhenNoStaticDiscountsAndFiftyPercentDiscount() {
        int actual = DiscountsUtil.calcTotal(
                List.of(Discount.builder().percentage(50.0).build()),
                List.of(Item.builder().price(100).build())
        );

        assertThat(actual).isEqualTo(50);
    }

    @Test
    void shouldReturnZeroWhenNoStaticDiscountsAndHundredPercentDiscount() {
        int actual = DiscountsUtil.calcTotal(
                List.of(Discount.builder().percentage(100.0).build()),
                List.of(Item.builder().price(100).build())
        );

        assertThat(actual).isZero();
    }

    @Test
    void shouldReturnTotalWhenNoStaticDiscountsAndZeroPercentDiscount() {
        int actual = DiscountsUtil.calcTotal(
                List.of(Discount.builder().percentage(0.0).build()),
                List.of(Item.builder().price(100).build())
        );

        assertThat(actual).isEqualTo(100);
    }

}