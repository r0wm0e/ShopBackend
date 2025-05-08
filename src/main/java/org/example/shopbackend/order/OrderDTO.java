package org.example.shopbackend.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OrderDTO {

    private final Long id;
    private final String status;
    private final double totalAmount;


}
