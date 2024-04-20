package com.qikserve.grocerystore.items;

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
class ItemsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private EntityPaths entityPaths;

    @InjectMocks
    private ItemsService itemsService;

    @BeforeEach
    void stubProp() {
        when(entityPaths.getItems()).thenReturn("/test");
    }

    @Test
    void shouldGetItemIfExist() {
        when(restTemplate.getForObject("/test/<test-id>", Item.class)).thenReturn(new Item());

        Item actualResponse = itemsService.getItem("<test-id>");

        assertThat(actualResponse).isNotNull();
    }

    @Nested
    class Create {
        @Test
        void shouldCreateAndReturnId() {
            ItemRequest request = ItemRequest.builder().name("test").build();
            when(restTemplate.postForObject("/test",
                            request,
                            Item.class
                    )
            ).thenReturn(Item.builder().id("<id>").build());

            String actualResult = itemsService.createItem(request);

            assertThat(actualResult).isEqualTo("<id>");
        }

        @Test
        void shouldErrorWhenCreateReturnsNull() {
            ItemRequest request = ItemRequest.builder().name("test").build();

            assertThatThrownBy(() -> itemsService.createItem(request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class Update {
        @Test
        void shouldCallUpdateAndReturnItemIfExists() {
            ItemRequest request = ItemRequest.builder().name("test").build();
            when(restTemplate.getForObject("/test/<test-id>", Item.class)).thenReturn(new Item());

            Item actualResult = itemsService.updateItem("<test-id>", request);

            assertThat(actualResult).isNotNull();
        }

        @Test
        void shouldErrorWhenItemDoesNotExist() {
            ItemRequest request = ItemRequest.builder().name("test").build();
            when(restTemplate.getForObject("/test/<test-id>", Item.class)).thenReturn(null);
            verifyNoMoreInteractions(restTemplate);

            assertThatThrownBy(() -> itemsService.updateItem("<test-id>", request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}