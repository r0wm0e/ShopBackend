package org.example.shopbackend.cart;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long cartId) {
        Cart cart = cartService.findById(cartId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/create")
    public ResponseEntity<Cart> createCart() {
        Cart cart = cartService.createCart();
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @PostMapping("/{cartId}/add")
    public ResponseEntity<Cart> addProductToCart(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
        Cart updatedCart = cartService.addProductToCart(cartId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/{cartId}/update")
    public ResponseEntity<Cart> updateQuantity(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
        Cart cart = cartService.updateQuantity(cartId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{cartId}/remove")
    public ResponseEntity<Cart> removeProduct(@PathVariable Long cartId, @RequestParam Long productId) {
        Cart cart = cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.ok(cart);
    }
}
