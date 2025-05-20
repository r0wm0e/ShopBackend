package org.example.shopbackend.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileOrderItemDTO {
    private Long id;
    private String productName;
    private String imageUrl;
    private int quantity;
    private double priceAtPurchase;
}
