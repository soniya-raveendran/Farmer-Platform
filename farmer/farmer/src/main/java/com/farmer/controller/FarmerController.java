package com.farmer.controller;

import com.farmer.entity.Product;
import com.farmer.entity.User;
import com.farmer.repository.ProductRepository;
import com.farmer.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/farmer")
@CrossOrigin(origins = "*")
public class FarmerController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public FarmerController(ProductRepository productRepository,
                            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ✅ ADD PRODUCT WITH IMAGE
    @PostMapping(value = "/{farmerId}/products", consumes = "multipart/form-data")
    public Product addProduct(
            @PathVariable Long farmerId,
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("quantity") int quantity,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        String uploadDir = "uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path path = Paths.get(uploadDir + fileName);
        Files.write(path, image.getBytes());

        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setImageUrl("uploads/" + fileName);
        product.setFarmer(farmer);

        return productRepository.save(product);
    }

    // ✅ VIEW FARMER PRODUCTS
    @GetMapping("/{farmerId}/products")
    public List<Product> getProducts(@PathVariable Long farmerId) {
        return productRepository.findByFarmerId(farmerId);
    }
}

