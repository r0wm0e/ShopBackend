package org.example.shopbackend.products;

import lombok.RequiredArgsConstructor;
import org.example.shopbackend.category.Category;
import org.example.shopbackend.category.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;


    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product createProduct(ProductDTO productDTO) {
        Category category = categoryService.findById(productDTO.getCategoryId());

        Product product = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .imageUrl(productDTO.getImageUrl())
                .category(category)
                .build();

        return productRepository.save(product);
    }
}
