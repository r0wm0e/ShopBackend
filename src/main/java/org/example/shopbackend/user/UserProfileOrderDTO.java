package org.example.shopbackend.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class UserProfileOrderDTO {
    private Long orderId;
    private String status;
    private double totalAmount;
    private LocalDateTime createdAt;
    private List<UserProfileOrderItemDTO> items;
}
