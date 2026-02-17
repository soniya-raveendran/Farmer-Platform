package com.farmer.service;

import com.farmer.entity.Feedback;
import java.util.List;

public interface FeedbackService {
    Feedback addFeedback(Long orderId, int rating, String comment);

    List<Feedback> getFarmerFeedback(Long farmerId);

    List<Feedback> getAllFeedback();
}
