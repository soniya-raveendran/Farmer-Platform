package com.farmer.controller;

import com.farmer.dto.LoginRequest;
import com.farmer.entity.User;
import com.farmer.request.RegisterRequest;
import com.farmer.response.LoginResponse;
import com.farmer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final com.farmer.service.EmailService emailService;
    private final com.farmer.service.OtpService otpService;

    public AuthController(UserService userService, com.farmer.service.EmailService emailService,
            com.farmer.service.OtpService otpService) {
        this.userService = userService;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        logger.info("Received login request for email: [{}]", req.getEmail());
        User user = userService.login(req.getEmail(), req.getPassword());

        String roleName = user.getRole() != null ? user.getRole().name() : "NONE";

        logger.info("Login successful for user: {}, role: {}", user.getEmail(), roleName);

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roleName);
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody java.util.Map<String, String> data) {
        String email = data.get("email");
        logger.info(">>> API /send-otp RECEIVED EMAIL: '{}'", email); // DEBUG LOG

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // 1. Check if email already exists (optional, but good practice)
        // For now, we just send OTP.

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
        return "OTP sent successfully";
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest req) {
        logger.info(">>> API /register RECEIVED REQUEST FOR EMAIL: '{}'", req.getEmail()); // DEBUG LOG
        // 1. Verify OTP first!
        String otp = req.getOtp(); // Assuming RegisterRequest has 'otp' field now.
        if (otp == null || otp.isEmpty()) {
            throw new RuntimeException("OTP is required");
        }

        boolean isValid = otpService.validateOtp(req.getEmail(), otp);
        if (!isValid) {
            throw new RuntimeException("Invalid or Expired OTP");
        }

        User user = userService.register(req);
        emailService.sendWelcomeEmail(user);
        return user;
    }
}
