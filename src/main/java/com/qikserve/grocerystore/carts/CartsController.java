package com.qikserve.grocerystore.carts;

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
@RequestMapping("/carts")
@RestController
public class CartsController {
    private final CartsService cartsService;

    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCart(final @PathVariable String id) {
        return ResponseEntity.ok(cartsService.getCart(id));
    }

    @PostMapping
    public ResponseEntity<Void> postCart(final @RequestBody CartRequest cartRequest) {
        String createdCartId = cartsService.createCart(cartRequest);
        return ResponseEntity.created(URI.create(createdCartId)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> putCart(final @PathVariable String id, final @RequestBody CartRequest cartRequest) {
        Cart updatedCart = cartsService.updateCart(id, cartRequest);
        return ResponseEntity.ok(updatedCart);
    }
}
