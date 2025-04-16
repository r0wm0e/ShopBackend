package org.example.shopbackend.cart;

import lombok.RequiredArgsConstructor;
import org.example.shopbackend.products.Product;
import org.example.shopbackend.products.ProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public Cart addProductToCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setProducts(new ArrayList<>());
                    newCart.setTotalAmount(0.0);
                    return cartRepository.save(newCart);
                });
        Product product = productService.findById(productId);

        product.setCart(cart);
        cart.getProducts().add(product);

        double totalAmount = cart.getProducts().stream()
                .mapToDouble(Product::getPrice)
                .sum();
        cart.setTotalAmount(totalAmount);

        return cartRepository.save(cart);
    }

    public Cart removeProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));


        Product products = productService.findById(productId);

        products.setCart(null);
        productService.save(products);

        double totalAmount = cart.getProducts().stream()
                .mapToDouble(Product::getPrice)
                .sum();
        cart.setTotalAmount(totalAmount);

        cartRepository.save(cart);
        return cart;
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
        Cart newCart = new Cart();
        return cartRepository.save(newCart);
    }
}
