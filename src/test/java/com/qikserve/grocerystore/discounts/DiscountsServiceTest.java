package com.qikserve.grocerystore.discounts;

import com.qikserve.grocerystore.config.EntityPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EntityPaths entityPaths;

    @InjectMocks
    private DiscountsService discountsService;

    @BeforeEach
    void stubProp() {
        when(entityPaths.getDiscounts()).thenReturn("/test");
    }

    @Test
    void shouldGetDiscountIfExists() {
        when(restTemplate.getForObject("/test/<test-id>", Discount.class)).thenReturn(new Discount());

        Discount actualResponse = discountsService.getDiscount("<test-id>");

        assertThat(actualResponse).isNotNull();
    }

    @Nested
    class Create {
        @Test
        void shouldCreateAndReturnId() {
            DiscountRequest request = DiscountRequest.builder().build();
            when(restTemplate.postForObject("/test",
                            request,
                            Discount.class
                    )
            ).thenReturn(Discount.builder().id("<id>").build());

            String actualResult = discountsService.createDiscount(request);

            assertThat(actualResult).isEqualTo("<id>");
        }

        @Test
        void shouldErrorWhenCreateReturnsNull() {
            DiscountRequest request = new DiscountRequest();

            assertThatThrownBy(() -> discountsService.createDiscount(request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class Update {
        @Test
        void shouldCallUpdateAndReturnDiscountIfExists() {
            DiscountRequest request = new DiscountRequest();
            when(restTemplate.getForObject("/test/<test-id>", Discount.class)).thenReturn(new Discount());

            Discount actualResult = discountsService.updateDiscount("<test-id>", request);

            assertThat(actualResult).isNotNull();
        }

        @Test
        void shouldErrorWhenDiscountDoesNotExist() {
            DiscountRequest request = new DiscountRequest();
            when(restTemplate.getForObject("/test/<test-id>", Discount.class)).thenReturn(null);
            verifyNoMoreInteractions(restTemplate);

            assertThatThrownBy(() -> discountsService.updateDiscount("<test-id>", request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

}