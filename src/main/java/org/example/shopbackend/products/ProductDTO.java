package org.example.shopbackend.products;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

    private String name;
    private String description;
    private Double price;
    private int stock;
    private String imageUrl;
    private Long categoryId;
}
