package org.example.shopbackend.integration;

import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map upload(byte[] data) {
        try {
            Map<String, String> options = new HashMap<>();
            Map uploadResult = cloudinary.uploader().upload(data, options);
            log.info("Image uploaded successfully: {}", uploadResult);
            return uploadResult;
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public Map uploadWithURL(String url) {
        try {
            return cloudinary.uploader().upload(url, new HashMap<>());
        } catch (IOException e) {
            log.error("Failed to upload image from URL", e);
            throw new RuntimeException("Failed to upload image from URL", e);
        }
    }

    public Map uploadByFile(File file) {
        try {
            return cloudinary.uploader().upload(file, new HashMap<>());
        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }
}
