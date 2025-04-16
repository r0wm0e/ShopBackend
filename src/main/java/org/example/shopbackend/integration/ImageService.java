package org.example.shopbackend.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class ImageService {

    private final CloudinaryService cloudinaryService;

    @Autowired
    public ImageService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    public String uploadImage(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            Map uploadResult = cloudinaryService.upload(file.getBytes());
            String imageUrl = (String) uploadResult.get("url");

            if (imageUrl == null || imageUrl.isEmpty()) {
                throw new RuntimeException("Image upload failed. No URL returned.");
            }

            log.info("Image uploaded successfully: {}", imageUrl);
            return imageUrl;

        } catch (IOException e) {
            log.error("IO error during image upload", e);
            throw new RuntimeException("IO error during image upload", e);
        } catch (Exception e) {
            log.error("Unexpected error during image upload", e);
            throw new RuntimeException("Unexpected error during image upload", e);
        }
    }
}
