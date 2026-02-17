package com.farmer.service.impl;

import com.farmer.dto.CartItemRequest;
import com.farmer.dto.CartOrderRequest;
import com.farmer.dto.OrderRequest;
import com.farmer.entity.*;
import com.farmer.repository.*;
import com.farmer.service.NotificationService;
import com.farmer.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;
    private final com.farmer.service.EmailService emailService;

    public OrderServiceImpl(OrderRepository orderRepo,
            OrderItemRepository orderItemRepo,
            ProductRepository productRepo,
            UserRepository userRepo,
            NotificationService notificationService,
            com.farmer.service.EmailService emailService) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Order placeOrder(OrderRequest request) {
        Product product = productRepo.findById(request.getProductId()).orElseThrow();
        User retailer = userRepo.findById(request.getRetailerId()).orElseThrow();

        if (request.getQuantity() > product.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        Order order = new Order();
        order.setRetailer(retailer);
        order.setStatus("PLACED");
        order.setPaymentStatus("PENDING");
        orderRepo.save(order);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        item.setPrice(product.getPrice());
        orderItemRepo.save(item);

        double total = product.getPrice() * request.getQuantity();
        order.setTotalAmount(total);

        product.setQuantity(product.getQuantity() - request.getQuantity());
        productRepo.save(product);

        // Notify Farmer
        notificationService.notify(
                "New Order #" + order.getId() + " received!",
                "FARMER",
                product.getFarmer().getId());

        // Email Notification
        emailService.sendOrderPlacedEmail(order);

        return order;
    }

    @Override
    @Transactional
    public Order placeCartOrder(CartOrderRequest request) {
        User retailer = userRepo.findById(request.getRetailerId()).orElseThrow();

        Order order = new Order();
        order.setRetailer(retailer);
        order.setStatus("PLACED");
        order.setPaymentStatus("PENDING");
        orderRepo.save(order);

        double grandTotal = 0;

        for (CartItemRequest cartItem : request.getItems()) {
            Product product = productRepo.findById(cartItem.getProductId()).orElseThrow();

            if (cartItem.getQuantity() > product.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(product.getPrice());
            orderItemRepo.save(item);

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepo.save(product);

            grandTotal += product.getPrice() * cartItem.getQuantity();

            // Notify Farmer (per product) - Might be spammy but simple
            notificationService.notify(
                    "New Order #" + order.getId() + " for " + product.getName(),
                    "FARMER",
                    product.getFarmer().getId());
        }

        order.setTotalAmount(grandTotal);
        Order savedOrder = orderRepo.save(order);

        // Email Notification
        emailService.sendOrderPlacedEmail(savedOrder);

        return savedOrder;
    }

    @Override
    public List<Order> getRetailerOrders(Long retailerId) {
        User retailer = userRepo.findById(retailerId).orElseThrow();
        return orderRepo.findByRetailer(retailer);
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        return orderItemRepo.findByOrder(order);
    }

    @Override
    public List<OrderItem> getFarmerOrders(Long farmerId) {
        return orderItemRepo.findByProduct_Farmer_Id(farmerId);
    }

    @Override
    @Transactional
    public String cancelOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("DELIVERED".equalsIgnoreCase(order.getStatus()) ||
                "SHIPPED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Cannot cancel this order");
        }

        order.setStatus("CANCELLED");

        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            order.setPaymentStatus("REFUNDED");
            order.setRefundStatus("INITIATED");
        } else {
            order.setPaymentStatus("CANCELLED");
        }

        orderRepo.save(order);

        List<OrderItem> items = orderItemRepo.findByOrder(order);
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepo.save(product);
        }

        // Notify Retailer
        notificationService.notify("Order #" + orderId + " cancelled", "RETAILER", order.getRetailer().getId());

        return "Order cancelled successfully";
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        order.setStatus(status.toUpperCase());
        orderRepo.save(order);

        // Retailer notification
        notificationService.notify("Your order #" + orderId + " is now " + status, "RETAILER",
                order.getRetailer().getId());

        // Admin notification
        notificationService.notify("Order #" + orderId + " updated to " + status, "ADMIN", 1L);

        return order;
    }

    @Override
    public Order updateTracking(Long orderId, String trackingNumber, String trackingCompany) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        order.setTrackingNumber(trackingNumber);
        order.setTrackingCompany(trackingCompany);
        orderRepo.save(order);

        // Retailer notification
        notificationService.notify("Tracking information updated for your order #" + orderId + ". " + trackingCompany
                + ": " + trackingNumber, "RETAILER", order.getRetailer().getId());

        return order;
    }
}
