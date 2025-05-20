package org.example.shopbackend.stripe;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shopbackend.order.OrderService;
import org.example.shopbackend.order.OrderStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final OrderService orderService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            log.error("Error constructing webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("payment_intent.created".equals(event.getType())) {
            event.getDataObjectDeserializer().getObject().ifPresent(obj -> {
                PaymentIntent paymentIntent = (PaymentIntent) obj;
                String stripeSessionId = paymentIntent.getId();

                orderService.updateOrderStatus(stripeSessionId, OrderStatus.PENDING);
                log.info("Order status updated to PENDING");
            });
        }

        if ("checkout.session.completed".equals(event.getType())) {
            event.getDataObjectDeserializer().getObject().ifPresent(obj -> {
                Session session = (Session) obj;
                String stripeSessionId = session.getId();

                orderService.updateOrderStatus(stripeSessionId, OrderStatus.PAID);
                log.info("Order status updated to PAID");
            });
        }

        if ("checkout.session.expired".equals(event.getType())) {
            event.getDataObjectDeserializer().getObject().ifPresent(obj -> {
                Session session = (Session) obj;
                String stripeSessionId = session.getId();

                orderService.updateOrderStatus(stripeSessionId, OrderStatus.CANCELLED);
                log.info("Order status updated to CANCELLED due to session expiration");
            });
        }

        if ("payment_intent.payment_failed".equals(event.getType())) {
            event.getDataObjectDeserializer().getObject().ifPresent(obj -> {
                PaymentIntent paymentIntent = (PaymentIntent) obj;
                String stripeSessionId = paymentIntent.getMetadata().get("stripeSessionId");

                orderService.updateOrderStatus(stripeSessionId, OrderStatus.FAILED);
                log.info("Order status updated to FAILED due to payment failure");
            });
        }



        return ResponseEntity.ok("Success");
    }
}
