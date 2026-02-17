package com.farmer.repository;

import com.farmer.entity.OrderItem;
import com.farmer.entity.Order;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByProduct_Farmer_Id(Long farmerId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi")
    Long sumQuantity();

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.paymentStatus = 'PAID'")
    Long sumTotalItemsSold();
}
