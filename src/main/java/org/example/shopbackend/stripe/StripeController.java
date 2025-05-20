package org.example.shopbackend.stripe;

import com.stripe.model.checkout.Session;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shopbackend.order.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/stripe")
public class StripeController {

    private final StripeService stripeService;
    private final OrderService orderService;

    @PostMapping("/create-checkout-session/cart/{cartId}")
    public ResponseEntity<String> createCheckoutSessionFromCart(@PathVariable Long cartId, Principal principal) {
        try {
            String username = principal.getName();

            Session session = stripeService.createCheckoutSessionFromCart(username, cartId);

            orderService.createOrderFromCart(cartId, username, session.getId());
            orderService.updateOrderStatus(session.getId(), OrderStatus.PENDING);

            return ResponseEntity.ok(session.getUrl());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public RedirectView success(@RequestParam("session_id") String sessionId) {

            orderService.updateOrderStatus(sessionId, OrderStatus.PAID);

            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("http://localhost:5173/success?session_id=" + sessionId);
            return redirectView;

    }

    @GetMapping("/order-details")
    public ResponseEntity<?> getOrderDetails(@RequestParam("session_id") String sessionId) {
        OrderDTO order = orderService.getOrderByStripeSessionId(sessionId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/canceled")
    public ResponseEntity<String> canceled(@RequestParam("session_id") String sessionId) {
        orderService.updateOrderStatus(sessionId, OrderStatus.CANCELLED);

        orderService.getOrderByStripeSessionId(sessionId);
        return ResponseEntity.ok("Hej Order cancelled with session_id: " + sessionId);
    }
}