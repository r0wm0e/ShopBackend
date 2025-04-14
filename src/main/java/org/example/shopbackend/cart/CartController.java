package org.example.shopbackend.cart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long cartId) {
        Cart cart = cartService.getCart(cartId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/")
    public ResponseEntity<Cart> getCart() {
        Cart cart = cartService.getCart(1L);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/create")
    public ResponseEntity<Cart> createCart() {
        Cart newCart = cartService.createCart();
        return ResponseEntity.status(HttpStatus.CREATED).body(newCart);
    }

    @PostMapping("/{cartId}/add-product/{productId}")
    public ResponseEntity<Cart> addProduct(@PathVariable Long cartId, @PathVariable Long productId) {
        Cart updatedCart = cartService.addProductToCart(cartId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{cartId}/remove-product/{productId}")
    public ResponseEntity<Cart> removeProduct(@PathVariable Long cartId, @PathVariable Long productId) {
        Cart updatedCart = cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.ok(updatedCart);
    }
}
