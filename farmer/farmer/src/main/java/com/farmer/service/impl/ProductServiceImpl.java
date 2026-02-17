package com.farmer.service.impl;

import com.farmer.entity.*;
import com.farmer.repository.FarmerDocumentRepository;
import com.farmer.repository.ProductRepository;
import com.farmer.repository.UserRepository;
import com.farmer.service.CloudinaryService;
import com.farmer.service.ProductService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final CloudinaryService cloudinaryService;
    private final FarmerDocumentRepository docRepo;

    public ProductServiceImpl(ProductRepository productRepo, UserRepository userRepo, CloudinaryService cloudinaryService, FarmerDocumentRepository docRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.cloudinaryService = cloudinaryService;
        this.docRepo = docRepo;
    }

    @Override
    public Product addProduct(String name, double price, int quantity, MultipartFile image, Long farmerId, String category) throws IOException {
        User farmer = userRepo.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        // 🔒 Verification Check
        FarmerDocument doc = docRepo.findByFarmer(farmer).orElse(null);
        if (doc == null || doc.getStatus() != VerificationStatus.APPROVED) {
             throw new RuntimeException("Action Denied! You must be a VERIFIED farmer to add products.");
        }

        String cloudUrl = cloudinaryService.uploadFile(image);

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCategory(category);
        product.setImageUrl(cloudUrl);
        product.setFarmer(farmer);

        return productRepo.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> getProductsByFarmer(Long farmerId) {
        return productRepo.findByFarmerId(farmerId);
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(updatedProduct.getName());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());
        return productRepo.save(existing);
    }

    @Override
    public String migrateOldImages() throws Exception {
        List<Product> products = productRepo.findAll();
        int updated = 0, skipped = 0, failed = 0;

        for (Product product : products) {
            String img = product.getImageUrl();
            if (img != null && img.startsWith("http")) {
                skipped++;
                continue;
            }
            if (img == null || img.isEmpty()) {
                failed++;
                continue;
            }
            String fileName = img.replace("/uploads/", "").trim();
            try {
                File file = new ClassPathResource("static/uploads/" + fileName).getFile();
                byte[] bytes = Files.readAllBytes(file.toPath());
                String cloudUrl = cloudinaryService.uploadBytes(bytes, fileName);
                product.setImageUrl(cloudUrl);
                productRepo.save(product);
                updated++;
            } catch (Exception e) {
                failed++;
            }
        }
        return "Migration Done: Updated=" + updated + " Skipped=" + skipped + " Failed=" + failed;
    }
}
