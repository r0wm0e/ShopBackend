package org.example.shopbackend.products;

import lombok.Data;

@Data
public class ProductDTO {

    private String name;
    private String description;
    private Double price;
    private int stock;
    private String imageUrl;
    private Long categoryId;
}
