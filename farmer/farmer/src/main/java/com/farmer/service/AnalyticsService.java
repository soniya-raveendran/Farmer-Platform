package com.farmer.service;

import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> getAdminStats();
    Map<String, Object> getFarmerStats(Long farmerId);
}
