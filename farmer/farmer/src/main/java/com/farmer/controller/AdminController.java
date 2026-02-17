package com.farmer.controller;

import com.farmer.entity.Product;
import com.farmer.entity.User;
import com.farmer.repository.OrderRepository;
import com.farmer.repository.OrderItemRepository;
import com.farmer.dto.AdminAnalytics;

import com.farmer.repository.ProductRepository;
import com.farmer.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public AdminController(UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // View all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // View all products
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ✅ DELETE USER
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User deleted successfully";
    }

    // ✅ BLOCK USER
    @PutMapping("/users/{id}/block")
    public String blockUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(false);
        userRepository.save(user);
        return "User blocked successfully";
    }

    // ✅ UNBLOCK USER
    @PutMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(true);
        userRepository.save(user);
        return "User unblocked successfully";
    }

    @GetMapping("/analytics")
    public AdminAnalytics getAnalytics() {

        AdminAnalytics analytics = new AdminAnalytics();

        long totalOrders = orderRepository.count();
        Double totalRevenue = orderRepository.sumTotalRevenue();
        Long totalItems = orderItemRepository.sumQuantity();

        analytics.setTotalOrders(totalOrders);
        analytics.setTotalRevenue(totalRevenue != null ? totalRevenue : 0);
        analytics.setTotalItemsSold(totalItems != null ? totalItems : 0);

        return analytics;
    }

}
