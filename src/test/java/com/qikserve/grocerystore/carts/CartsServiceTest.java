package com.qikserve.grocerystore.carts;

import com.qikserve.grocerystore.config.EntityPaths;
import com.qikserve.grocerystore.discounts.Discount;
import com.qikserve.grocerystore.discounts.DiscountsService;
import com.qikserve.grocerystore.items.Item;
import com.qikserve.grocerystore.items.ItemsService;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartsServiceTest {

    @Mock
    private DiscountsService discountsService;
    @Mock
    private EntityPaths entityPaths;
    @Mock
    private ItemsService itemsService;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CartsService cartsService;

    @Test
    void shouldGetCartItemsAndDiscounts() {
        CartModel mockResponse = CartModel.builder()
                .id("<id>")
                .total(Integer.MAX_VALUE)
                .discounts(List.of("<discount-id>"))
                .items(List.of("<item-id>"))
                .build();
        when(entityPaths.getCarts()).thenReturn("/test");
        when(restTemplate.getForObject("/test/<test-id>", CartModel.class)).thenReturn(mockResponse);
        when(discountsService.getDiscount("<discount-id>")).thenReturn(Discount.builder().description("some discount").build());
        when(itemsService.getItem("<item-id>")).thenReturn(Item.builder().name("some item").build());

        Cart actualResponse = cartsService.getCart("<test-id>");

        assertThat(actualResponse).isNotNull();

        assertThat(actualResponse)
                .extracting(Cart::getId).isEqualTo("<id>");

        assertThat(actualResponse)
                .extracting(Cart::getTotal).isEqualTo(Integer.MAX_VALUE);

        assertThat(actualResponse)
                .extracting(Cart::getDiscounts, InstanceOfAssertFactories.list(Discount.class))
                .extracting(Discount::getDescription)
                .containsExactly("some discount");

        assertThat(actualResponse)
                .extracting(Cart::getItems, InstanceOfAssertFactories.list(Item.class))
                .extracting(Item::getName)
                .containsExactly("some item");
    }

    @Nested
    class Create {
        @Test
        void shouldCallCreate() {
            when(entityPaths.getCarts()).thenReturn("/test");
            CartRequest cartRequest = CartRequest.builder()
                    .discountIds(emptyList())
                    .itemIds(emptyList())
                    .build();

            CartEntityRequest expectedOutboundRequest = CartEntityRequest.builder()
                    .discountIds(emptyList())
                    .itemIds(emptyList())
                    .total(0)
                    .build();

            when(restTemplate.postForObject("/test", expectedOutboundRequest, CartModel.class))
                    .thenReturn(
                            CartModel.builder()
                                    .id("<cart-created>")
                                    .build()
                    );

            String actualResult = cartsService.createCart(cartRequest);

            assertThat(actualResult).isEqualTo("<cart-created>");
        }

        @ParameterizedTest
        @MethodSource("createWithDiscounts")
        void shouldCalculateDiscountAndCreate(final Item item, final Discount discount, final Integer expectedTotal) {
            when(entityPaths.getCarts()).thenReturn("/test");
            when(itemsService.getItem("<item-id>")).thenReturn(item);
            when(discountsService.getDiscount("<discount-id>")).thenReturn(discount);

            CartRequest cartRequest = CartRequest.builder()
                    .itemIds(List.of("<item-id>"))
                    .discountIds(List.of("<discount-id>"))
                    .build();

            CartEntityRequest expectedOutboundRequest = CartEntityRequest.builder()
                    .itemIds(List.of("<item-id>"))
                    .discountIds(List.of("<discount-id>"))
                    .total(expectedTotal)
                    .build();

            when(restTemplate.postForObject("/test", expectedOutboundRequest, CartModel.class))
                    .thenReturn(
                            CartModel.builder()
                                    .id("<cart-created>")
                                    .build()
                    );

            String actualResult = cartsService.createCart(cartRequest);

            assertThat(actualResult).isEqualTo("<cart-created>");
        }

        public static List<Arguments> createWithDiscounts() {
            return List.of(
                    Arguments.of(
                            Item.builder().price(100).build(),
                            Discount.builder().staticAmount(10).build(),
                            90
                    ),
                    Arguments.of(
                            Item.builder().price(100).build(),
                            Discount.builder().staticAmount(101).build(),
                            0
                    ),
                    Arguments.of(
                            Item.builder().price(100).build(),
                            Discount.builder().percentage(50.0).build(),
                            50
                    )
            );
        }

        @Test
        void shouldThrowWhenInvalidItemId() {
            when(itemsService.getItem("<invalid-item-id>")).thenReturn(null);
            CartRequest cartRequest = CartRequest.builder().itemIds(List.of("<invalid-item-id>")).build();

            assertThatThrownBy(() -> cartsService.createCart(cartRequest))
                    .withFailMessage(() -> "Item not found <invalid-item-id>")
                    .isInstanceOf(NoSuchElementException.class);

            verifyNoInteractions(discountsService, restTemplate);
        }

        @Test
        void shouldThrowWhenInvalidDiscountId() {
            when(discountsService.getDiscount("<invalid-discount-id>")).thenReturn(null);
            CartRequest cartRequest = CartRequest.builder().discountIds(List.of("<invalid-discount-id>")).build();

            assertThatThrownBy(() -> cartsService.createCart(cartRequest))
                    .withFailMessage(() -> "Discount not found <invalid-discount-id>")
                    .isInstanceOf(NoSuchElementException.class);

            verifyNoInteractions(itemsService, restTemplate);
        }
    }

    @Nested
    class Update {
        @Test
        void shouldCallUpdateAndReturnIfExists() {
            when(entityPaths.getCarts()).thenReturn("/test");
            CartRequest cartRequest = CartRequest.builder().build();
            when(restTemplate.getForObject("/test/<cart-id>", CartModel.class))
                    .thenReturn(CartModel.builder().discounts(emptyList()).items(emptyList()).build());

            Cart actualResult = cartsService.updateCart("<cart-id>", cartRequest);

            assertThat(actualResult).isNotNull();
        }

        @Test
        void shouldErrorWhenCartDoesNotExist() {
            when(entityPaths.getCarts()).thenReturn("/test");
            CartRequest cartRequest = CartRequest.builder().build();
            when(restTemplate.getForObject("/test/<cart-id>", CartModel.class))
                    .thenReturn(null);

            assertThatThrownBy(() -> cartsService.updateCart("<cart-id>", cartRequest))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

}