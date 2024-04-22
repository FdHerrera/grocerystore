package com.qikserve.grocerystore.items;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@AllArgsConstructor
@RequestMapping("/items")
@RestController
public class ItemsController {
    private final ItemsService itemsService;


    @GetMapping("/{id}")
    public ResponseEntity<Item> getItems(final @PathVariable String id) {
        return ResponseEntity.ok(itemsService.getItem(id));
    }

    @PostMapping
    public ResponseEntity<Void> postItems(final @RequestBody ItemRequest itemRequest) {
        String createdItemId = itemsService.createItem(itemRequest);
        return ResponseEntity.created(URI.create(createdItemId)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> putItems(
            final @PathVariable String id,
            final @RequestBody ItemRequest itemRequest
    ) {
        Item updatedItem = itemsService.updateItem(id, itemRequest);
        return ResponseEntity.ok(updatedItem);
    }
}
