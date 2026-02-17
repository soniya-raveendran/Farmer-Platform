package com.farmer.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                "folder", "farmer_products"
        ));

        return uploadResult.get("secure_url").toString();
    }
    public String uploadBytes(byte[] bytes, String fileName) throws Exception {
        Map uploadResult = cloudinary.uploader().upload(bytes,
                ObjectUtils.asMap("folder", "farmer_products"));

        return uploadResult.get("secure_url").toString();
    }
}
