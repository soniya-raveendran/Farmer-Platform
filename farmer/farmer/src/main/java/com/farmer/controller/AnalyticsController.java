package com.farmer.controller;

import com.farmer.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin("*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/admin")
    public Map<String, Object> getAdminStats() {
        return analyticsService.getAdminStats();
    }

    @GetMapping("/farmer/{farmerId}")
    public Map<String, Object> getFarmerStats(@PathVariable Long farmerId) {
        return analyticsService.getFarmerStats(farmerId);
    }
}
