package org.example.shopbackend.stripe;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StripeCheckoutSessionResponse {
    private String sessionId;
    private String paymentStatus;
    private long amountTotal;
    private String currency;
    private String status;
    private String customerEmail;
}
