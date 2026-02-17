package com.farmer.controller;

import com.farmer.entity.Order;
import com.farmer.entity.OrderItem;
import com.farmer.repository.OrderItemRepository;
import com.farmer.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin("*")
public class AdminOrderController {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    public AdminOrderController(OrderRepository orderRepo,
                                OrderItemRepository orderItemRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
    }

    // ✅ GET ALL ORDERS (Admin)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    // ✅ GET ORDER ITEMS
    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        return orderItemRepo.findByOrder(order);
    }
}
