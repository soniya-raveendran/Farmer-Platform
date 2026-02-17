package com.farmer.service.impl;

import com.farmer.entity.Role;
import com.farmer.entity.VerificationStatus;
import com.farmer.repository.FarmerDocumentRepository;
import com.farmer.repository.OrderItemRepository;
import com.farmer.repository.OrderRepository;
import com.farmer.repository.UserRepository;
import com.farmer.service.AnalyticsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final FarmerDocumentRepository docRepo;
    private final OrderItemRepository orderItemRepo;

    public AnalyticsServiceImpl(UserRepository userRepo, OrderRepository orderRepo,
            FarmerDocumentRepository docRepo, OrderItemRepository orderItemRepo) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.docRepo = docRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @Override
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepo.count());
        stats.put("totalFarmers", userRepo.countByRole(Role.FARMER));
        stats.put("totalRetailers", userRepo.countByRole(Role.RETAILER));

        // Verified Farmers count
        long verified = docRepo.findAll().stream()
                .filter(d -> d.getStatus() == VerificationStatus.APPROVED)
                .count();
        stats.put("verifiedFarmers", verified);

        stats.put("totalRevenue", orderRepo.sumTotalRevenue() != null ? orderRepo.sumTotalRevenue() : 0.0);
        stats.put("totalItemsSold", orderItemRepo.sumTotalItemsSold() != null ? orderItemRepo.sumTotalItemsSold() : 0L);

        // Monthly Sales
        List<Object[]> monthlySales = orderRepo.findMonthlySales();
        // Convert to List of Maps
        List<Map<String, Object>> salesData = monthlySales.stream().map(row -> {
            Map<String, Object> m = new HashMap<>();
            m.put("month", row[0]);
            m.put("sales", row[1]);
            return m;
        }).collect(Collectors.toList());
        stats.put("monthlySales", salesData);

        return stats;
    }

    @Override
    public Map<String, Object> getFarmerStats(Long farmerId) {
        Map<String, Object> stats = new HashMap<>();

        // Simple logic for Farmer stats: Sum of items sold by this farmer
        // Ideally we need custom queries for "Revenue per farmer"
        // But for time being, let's fetch items and sum up.

        var items = orderItemRepo.findByProduct_Farmer_Id(farmerId);

        double totalRevenue = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        long totalItemsSold = items.stream().mapToLong(i -> i.getQuantity()).sum();
        long totalOrders = items.stream().map(i -> i.getOrder().getId()).distinct().count();

        stats.put("totalRevenue", totalRevenue);
        stats.put("totalItemsSold", totalItemsSold);
        stats.put("totalOrders", totalOrders);

        // Group by product
        Map<String, Double> productSales = items.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getProduct().getName(),
                        Collectors.summingDouble(i -> i.getPrice() * i.getQuantity())));
        stats.put("productSales", productSales);

        return stats;
    }
}
