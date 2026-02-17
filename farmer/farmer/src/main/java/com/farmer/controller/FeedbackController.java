package com.farmer.controller;

import com.farmer.entity.Feedback;
import com.farmer.service.FeedbackService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin("*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/add")
    public Feedback addFeedback(@RequestBody Map<String, Object> payload) {
        Long orderId = Long.valueOf(payload.get("orderId").toString());
        int rating = Integer.parseInt(payload.get("rating").toString());
        String comment = (String) payload.get("comment");

        return feedbackService.addFeedback(orderId, rating, comment);
    }

    @GetMapping("/farmer/{farmerId}")
    public List<Feedback> getFarmerFeedback(@PathVariable Long farmerId) {
        return feedbackService.getFarmerFeedback(farmerId);
    }

    @GetMapping("/all")
    public List<Feedback> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }
}
