package org.example.shopbackend.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.example.shopbackend.integration.CloudinaryService;
import org.example.shopbackend.integration.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    private final ImageService imageService;

    @GetMapping("/all")
    public List<Product> getProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO product) {
        try {
            Product savedProduct = productService.createProduct(product);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating product {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/createWithImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(@RequestPart("product") String productJson,
        @RequestPart(value = "image", required = false) MultipartFile imageFile) {

    try {
        ObjectMapper mapper = new ObjectMapper();
        ProductDTO productDTO = mapper.readValue(productJson, ProductDTO.class);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageService.uploadImage(imageFile);
            productDTO.setImageUrl(imageUrl);
        }

        Product savedProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    } catch (IOException e) {
        log.error("Error creating product {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}


    @PutMapping(value = "/id/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> uploadProductImage(@PathVariable Long id, @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            Product product = productService.findById(id);
            Map result = cloudinaryService.upload(imageFile.getBytes());

            String imageUrl = (String) result.get("url");
            if (Strings.isEmpty(imageUrl)) {
                throw new Exception("Missing image URL");
            }

            product.setImageUrl(imageUrl);
            Product updatedProduct = productService.save(product);
            return ResponseEntity.ok(updatedProduct);

        } catch (Exception e) {
            log.error("Error uploading image {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
