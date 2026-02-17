package com.farmer.controller;

import com.farmer.entity.Product;
import com.farmer.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public Product addProduct(
            @RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("quantity") int quantity,
            @RequestParam("image") MultipartFile image,
            @RequestParam("farmerId") Long farmerId,
            @RequestParam("category") String category
    ) throws IOException {
        return productService.addProduct(name, price, quantity, image, farmerId, category);
    }

    @GetMapping("/all")
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/farmer/{farmerId}")
    public List<Product> getProductsByFarmer(@PathVariable Long farmerId) {
        return productService.getProductsByFarmer(farmerId);
    }

    @PutMapping("/update/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }

    @PostMapping("/migrate-old-images")
    public String migrateOldImagesToCloudinary() throws Exception {
        return productService.migrateOldImages();
    }
}
