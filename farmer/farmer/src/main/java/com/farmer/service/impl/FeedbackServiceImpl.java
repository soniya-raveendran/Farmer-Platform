package com.farmer.service.impl;

import com.farmer.entity.*;
import com.farmer.repository.FeedbackRepository;
import com.farmer.repository.OrderRepository;
import com.farmer.repository.UserRepository;
import com.farmer.service.FeedbackService;
import com.farmer.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepo;
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepo, OrderRepository orderRepo,
            UserRepository userRepo,
            NotificationService notificationService) {
        this.feedbackRepo = feedbackRepo;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
    }

    @Override
    public Feedback addFeedback(Long orderId, int rating, String comment) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"DELIVERED".equalsIgnoreCase(order.getStatus()) && !"COMPLETED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("You can only rate completed or delivered orders.");
        }

        // Logic to attribute feedback to a farmer.
        // Orders can have multiple items from multiple farmers?
        // The Feedback entity seems to have `Use retailer` and `User farmer`.
        // If an order has multiple farmers, feedback is tricky.
        // Assuming Order structure: Order -> Items -> Product -> Farmer.
        // If Order has mixed items, we might pick the first farmer or loop?
        // Let's assume Simplicity: 1 Farmer per Order or Feedback is per Order
        // generally.
        // But Feedback Entity has `farmer`.
        // Let's attach to the first item's farmer for now or throw error if mixed.
        // Requirement: "Feedback linked to Order, Product, Farmer".
        // The implementation Plan said "Simplify".

        // I will take the farmer and product from the first item.
        OrderItem item = order.getItems().get(0);
        User farmer = item.getProduct().getFarmer();
        Product product = item.getProduct();

        Feedback feedback = new Feedback();
        feedback.setOrder(order);
        feedback.setRetailer(order.getRetailer());
        feedback.setFarmer(farmer);
        feedback.setProduct(product);
        feedback.setRating(rating);
        feedback.setComment(comment);

        Feedback saved = feedbackRepo.save(feedback);

        // Notify ALL farmers involved in the order
        order.getItems().stream()
                .map(i -> i.getProduct().getFarmer())
                .distinct()
                .forEach(f -> {
                    notificationService.notify(
                            "You received a " + rating + "* rating for products in Order #" + orderId, "FARMER",
                            f.getId());
                });

        // Notify ALL Admins
        orderRepo.findById(orderId).ifPresent(o -> {
            userRepo.findByRole(Role.ADMIN).forEach(admin -> {
                notificationService.notify("Order #" + orderId + " received a " + rating + "* rating.", "ADMIN",
                        admin.getId());
            });
        });

        return saved;
    }

    @Override
    public List<Feedback> getFarmerFeedback(Long farmerId) {
        return feedbackRepo.findByFarmerId(farmerId);
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepo.findAll();
    }
}
