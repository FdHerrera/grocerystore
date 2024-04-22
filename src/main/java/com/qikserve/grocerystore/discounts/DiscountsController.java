package com.qikserve.grocerystore.discounts;

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
@RequestMapping("/discounts")
@RestController
public class DiscountsController {
    private final DiscountsService discountsService;

    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscount(final @PathVariable String id) {
        return ResponseEntity.ok(discountsService.getDiscount(id));
    }

    @PostMapping
    public ResponseEntity<Void> postDiscount(final @RequestBody DiscountRequest discountRequest) {
        String createdDiscountId = discountsService.createDiscount(discountRequest);
        return ResponseEntity.created(URI.create(createdDiscountId)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discount> putDiscount(
            final @PathVariable String id,
            final @RequestBody DiscountRequest discountRequest
    ) {
        Discount updatedDiscount = discountsService.updateDiscount(id, discountRequest);
        return ResponseEntity.ok(updatedDiscount);
    }
}
