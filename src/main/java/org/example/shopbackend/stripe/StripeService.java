package org.example.shopbackend.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shopbackend.cart.Cart;
import org.example.shopbackend.cart.CartItem;
import org.example.shopbackend.cart.CartService;
import org.example.shopbackend.products.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    private final CartService cartService;

    @Value("${stripe.api.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public Session createCheckoutSessionFromCart(String username, Long cartId) throws StripeException {

        Cart cart = cartService.findById(cartId);
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();


        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();

            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(product.getName())
                            .setDescription(product.getDescription())
                            .build();

            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("sek")
                            .setUnitAmount((long) (product.getPrice() * 100))
                            .setProductData(productData)
                            .build();

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getQuantity())
                            .setPriceData(priceData)
                            .build();

            lineItems.add(lineItem);
        }

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/api/stripe/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:5173/")
                .putMetadata("cartId", cartId.toString())
                .putMetadata("username", username);

        for(SessionCreateParams.LineItem lineItem : lineItems) {
            builder.addLineItem(lineItem);
        }

        return Session.create(builder.build());
    }
}
