package org.example.shopbackend.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    private final CloudinaryService cloudinaryService;

    @Autowired
    public ImageService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinaryService.upload(file.getBytes());
        String imageUrl = (String) uploadResult.get("url");

        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new RuntimeException("Image upload failed. No URL returned.");
        }

        return imageUrl;
    }
}
