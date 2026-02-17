package com.farmer.controller;

import com.farmer.dto.CartOrderRequest;
import com.farmer.dto.OrderRequest;
import com.farmer.entity.Order;
import com.farmer.entity.OrderItem;
import com.farmer.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public Order placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @PostMapping("/place-cart")
    public Order placeCartOrder(@RequestBody CartOrderRequest request) {
        return orderService.placeCartOrder(request);
    }

    @GetMapping("/retailer/{retailerId}")
    public List<Order> getRetailerOrders(@PathVariable Long retailerId) {
        return orderService.getRetailerOrders(retailerId);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItems(orderId);
    }

    @GetMapping("/farmer/{farmerId}")
    public List<OrderItem> getFarmerOrders(@PathVariable Long farmerId) {
        return orderService.getFarmerOrders(farmerId);
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    @PutMapping("/{orderId}/status")
    public Order updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @PutMapping("/{orderId}/tracking")
    public Order updateTracking(@PathVariable Long orderId, @RequestParam String trackingNumber,
            @RequestParam String trackingCompany) {
        return orderService.updateTracking(orderId, trackingNumber, trackingCompany);
    }
}
