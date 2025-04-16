package org.example.shopbackend.products;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shopbackend.category.CategoryService;
import org.example.shopbackend.integration.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ImageService imageService;

    @GetMapping("/all")
    public List<Product> getProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

//    @PostMapping("/create")
//    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO product) {
//        Product savedProduct = productService.createProduct(product);
//        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
//    }
@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Product> createProduct(
        @RequestPart("product") ProductDTO productDTO,
        @RequestPart("image") MultipartFile imageFile) {
    log.info("Trying to create product");
    try {
        String imageUrl = imageService.uploadImage(imageFile);
        productDTO.setImageUrl(imageUrl);
        Product savedProduct = productService.createProduct(productDTO);
        log.info("Saving product");
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    } catch (Exception e) {
        e.printStackTrace();
        log.error("SOMETHING WENT WRONG");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}


    @PutMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadProductImage(
            @PathVariable Long id,
            @RequestPart MultipartFile file) {
        try {
            String imageUrl = imageService.uploadImage(file);
            // Todo update product with image
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image");
        }
    }
}
