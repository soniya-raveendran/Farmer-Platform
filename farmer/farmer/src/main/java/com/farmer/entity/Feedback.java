package com.farmer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private User retailer;

    @ManyToOne
    private User farmer;

    @ManyToOne
    private Product product;

    private int rating; // 1–5

    @Column(length = 500)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public User getRetailer() {
        return retailer;
    }

    public User getFarmer() {
        return farmer;
    }

    public Product getProduct() {
        return product;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setRetailer(User retailer) {
        this.retailer = retailer;
    }

    public void setFarmer(User farmer) {
        this.farmer = farmer;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
