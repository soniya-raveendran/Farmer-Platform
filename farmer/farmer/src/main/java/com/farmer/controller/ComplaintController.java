package com.farmer.controller;

import com.farmer.entity.Complaint;
import com.farmer.entity.User;
import com.farmer.repository.ComplaintRepository;
import com.farmer.repository.OrderRepository;
import com.farmer.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin("*")
public class ComplaintController {

    private final ComplaintRepository complaintRepo;
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final com.farmer.service.EmailService emailService;

    public ComplaintController(ComplaintRepository complaintRepo, UserRepository userRepo, OrderRepository orderRepo,
            com.farmer.service.EmailService emailService) {
        this.complaintRepo = complaintRepo;
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.emailService = emailService;
    }

    @PostMapping("/add")
    public Complaint addComplaint(@RequestBody Map<String, Object> data) {
        if (data == null || !data.containsKey("retailerId") || data.get("retailerId") == null) {
            throw new RuntimeException("Retailer ID is required");
        }

        Long retailerId = data.get("retailerId") instanceof Number
                ? ((Number) data.get("retailerId")).longValue()
                : Long.valueOf(data.get("retailerId").toString());

        String message = data.get("message") != null ? data.get("message").toString() : "";

        Complaint c = new Complaint();
        c.setRetailer(userRepo.findById(retailerId).orElseThrow(() -> new RuntimeException("Retailer not found")));
        c.setMessage(message);

        Object orderIdObj = data.get("orderId");
        if (orderIdObj != null && !orderIdObj.toString().isEmpty()) {
            try {
                Long orderId = orderIdObj instanceof Number
                        ? ((Number) orderIdObj).longValue()
                        : Long.valueOf(orderIdObj.toString());
                c.setOrder(orderRepo.findById(orderId).orElse(null));
            } catch (Exception e) {
                // Ignore invalid order ID
            }
        }

        return complaintRepo.save(c);
    }

    @GetMapping("/retailer/{id}")
    public List<Complaint> getByRetailer(@PathVariable Long id) {
        User retailer = userRepo.findById(id).orElseThrow();
        return complaintRepo.findByRetailer(retailer);
    }

    @GetMapping("/all")
    public List<Complaint> getAll() {
        return complaintRepo.findAll();
    }

    @PutMapping("/{id}/resolve")
    public Complaint resolve(@PathVariable Long id, @RequestBody Map<String, String> data) {
        Complaint c = complaintRepo.findById(id).orElseThrow();
        c.setResponse(data.get("response"));
        c.setStatus("RESOLVED");
        Complaint saved = complaintRepo.save(c);

        // Send Email Notification
        emailService.sendComplaintResolvedEmail(saved);

        return saved;
    }
}
