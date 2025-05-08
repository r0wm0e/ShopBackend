package org.example.shopbackend.cart;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shopbackend.products.Product;
import org.example.shopbackend.products.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Cart addProductToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock");
        }


        CartItem existingItem = null;
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;

            if (product.getStock() < newQuantity) {
                log.error("Not enough stock to add product {}, quantity {}", productId, quantity);
                throw new IllegalArgumentException("Not enough stock for updated quantity");
            }
            existingItem.setQuantity(newQuantity);
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

        CartItem foundItem = null;
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem == null) {
            throw new IllegalArgumentException("Product not found");
        }

        if (newQuantity <= 0) {
            cart.getItems().remove(foundItem);
        } else {
            if (foundItem.getProduct().getStock() < newQuantity) {
                throw new IllegalArgumentException("Not enough stock for product ID " + productId);
            }
            foundItem.setQuantity(newQuantity);
        }

        cart.calculateTotalAmount();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem itemToRemove = null;
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            cart.getItems().remove(itemToRemove);
        }

        cart.calculateTotalAmount();
        return cartRepository.save(cart);
    }

    public Cart findById(Long cartId) {
        return cartRepository.findById(cartId).orElseGet(() -> {
            Cart newCart = new Cart();
            cartRepository.save(newCart);
            return newCart;
        });
    }

    public Cart createCart() {
        Cart newCart = Cart.builder().build();
        return cartRepository.save(newCart);
    }
    public void save(Cart newCart) {
        cartRepository.save(newCart);
    }
}
