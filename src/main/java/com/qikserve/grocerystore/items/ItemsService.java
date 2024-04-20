package com.qikserve.grocerystore.items;

import com.qikserve.grocerystore.config.EntityPaths;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Objects.isNull;

@AllArgsConstructor
@Service
public class ItemsService {
    private final EntityPaths entityPaths;
    private final RestTemplate restTemplate;

    public Item getItem(final String id) {
        return restTemplate.getForObject(
                entityPaths.getItems().concat("/").concat(id),
                Item.class
        );
    }

    public String createItem(final ItemRequest itemRequest) {
        Item createdItem = restTemplate.postForObject(
                entityPaths.getItems(),
                itemRequest,
                Item.class
        );
        return Optional.ofNullable(createdItem)
                .map(Item::getId)
                .orElseThrow();
    }

    public Item updateItem(final String id, final ItemRequest itemRequest) {
        if (isNull(getItem(id))) {
            throw new NoSuchElementException();
        }
        restTemplate.put(
                entityPaths.getItems().concat("/").concat(id),
                itemRequest
        );
        return getItem(id);
    }
}