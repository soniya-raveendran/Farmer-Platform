package com.farmer.service;

import com.farmer.dto.CartOrderRequest;
import com.farmer.dto.OrderRequest;
import com.farmer.entity.Order;
import com.farmer.entity.OrderItem;
import java.util.List;

public interface OrderService {

    Order placeOrder(OrderRequest request);

    Order placeCartOrder(CartOrderRequest request);

    List<Order> getRetailerOrders(Long retailerId);

    List<OrderItem> getOrderItems(Long orderId);

    List<OrderItem> getFarmerOrders(Long farmerId);

    String cancelOrder(Long orderId);

    Order updateOrderStatus(Long orderId, String status);

    Order updateTracking(Long orderId, String trackingNumber, String trackingCompany);
}
