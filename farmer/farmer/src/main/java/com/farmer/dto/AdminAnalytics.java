package com.farmer.dto;

public class AdminAnalytics {

    private long totalOrders;
    private double totalRevenue;
    private long totalItemsSold;

    public long getTotalOrders() {
        return totalOrders;
    }
    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalItemsSold() {
        return totalItemsSold;
    }
    public void setTotalItemsSold(long totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }
}
