package com.farmer.repository;

import com.farmer.entity.Order;
import com.farmer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRetailer(User retailer);

    Order findByRazorpayOrderId(String razorpayOrderId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    Double sumTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o")
    long countTotalOrders();

    // Simple monthly grouping (Native Query for MySQL compatibility)
    @Query(value = "SELECT MONTHNAME(order_date) as month, SUM(total_amount) as sales FROM orders WHERE payment_status = 'PAID' GROUP BY MONTH(order_date), MONTHNAME(order_date) ORDER BY MONTH(order_date)", nativeQuery = true)
    List<Object[]> findMonthlySales();
}
