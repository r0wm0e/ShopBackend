package org.example.shopbackend.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shopbackend.cart.Cart;
import org.example.shopbackend.cart.CartItem;
import org.example.shopbackend.cart.CartService;
import org.example.shopbackend.products.Product;
import org.example.shopbackend.products.ProductService;
import org.example.shopbackend.user.User;
import org.example.shopbackend.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final ProductService productService;

    @Transactional
    public void createOrderFromCart(Long cartId, String username, String stripeSessionId) {
        Cart cart = cartService.findById(cartId);
        User user = userService.findByUsername(username);

        buildAndSaveOrder(cart, user, stripeSessionId);
        clearCart(cart);
    }

    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartService.save(cart);
    }

    private void buildAndSaveOrder(Cart cart, User user, String stripeSessionId) {
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PAID)
                .totalAmount(cart.getTotalAmount())
                .stripeSessionId(stripeSessionId)
                .createdAt(LocalDateTime.now())
                .build();

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int newStock = product.getStock() - item.getQuantity();

            if (newStock < 0) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }

            product.setStock(newStock);
            productService.save(product);

            OrderItem orderItem = buildOrderItem(item, order);
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        orderRepository.save(order);
    }

    private OrderItem buildOrderItem(CartItem item, Order order) {
        return OrderItem.builder()
                .order(order)
                .product(item.getProduct())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getProduct().getPrice())
                .build();
    }

    public void updateOrderStatus(String stripeSessionId, OrderStatus newStatus) {
        log.info("Updating order status for {}", stripeSessionId);

        Order order = orderRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        log.info("Order found: id={}, status={}", order.getId(), newStatus);

        order.setStatus(newStatus);
        orderRepository.save(order);

        log.info("Order updated to {}", newStatus);
    }

    public OrderDTO getOrderByStripeSessionId(String stripeSessionId) {
        Order order = orderRepository.findByStripeSessionId(stripeSessionId).orElseThrow(() -> new RuntimeException("Order not found"));

        return new OrderDTO(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount()
        );
    }
}
