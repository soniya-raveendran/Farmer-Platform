package com.farmer.service;

import com.farmer.entity.Product;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface ProductService {

    Product addProduct(String name, double price, int quantity, MultipartFile image, Long farmerId, String category) throws IOException;

    List<Product> getAllProducts();

    Product getProductById(Long id);

    List<Product> getProductsByFarmer(Long farmerId);

    Product updateProduct(Long id, Product updatedProduct);

    String migrateOldImages() throws Exception;
}
