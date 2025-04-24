package org.example.shopbackend.cart;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.shopbackend.products.Product;
import org.example.shopbackend.products.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public Cart addProductToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock");
        }


        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getStock() < newQuantity) {
                throw new IllegalArgumentException("Not enough stock for updated quantity");
            }
            item.setQuantity(newQuantity);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(item);
        }

        cart.calculateTotalAmount();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(Long cartId, Long productId, int newQuantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not in cart"));

        if (newQuantity <= 0) {
            cart.getItems().remove(item);
        } else {
            if (item.getProduct().getStock() < newQuantity) {
                throw new IllegalArgumentException("Not enough stock for product ID " + productId);
            }
            item.setQuantity(newQuantity);
        }

        cart.calculateTotalAmount();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cart.calculateTotalAmount();
        return cartRepository.save(cart);
    }

    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    cartRepository.save(newCart);
                    return newCart;
                });
    }

    public Cart createCart() {
        Cart newCart = Cart.builder().build();
        return cartRepository.save(newCart);
    }
}
