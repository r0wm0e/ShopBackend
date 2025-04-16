package org.example.shopbackend.integration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map upload(byte[] data) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());
    }

    public Map uploadWithURL(String url) throws IOException {
        return cloudinary.uploader().upload(url, ObjectUtils.emptyMap());
    }

    public Map uploadByFile(File file) throws IOException {
        return cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
    }
}
