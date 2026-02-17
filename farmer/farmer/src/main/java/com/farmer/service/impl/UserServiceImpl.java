package com.farmer.service.impl;

import com.farmer.entity.Role;
import com.farmer.entity.User;
import com.farmer.repository.UserRepository;
import com.farmer.request.RegisterRequest;
import com.farmer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepo;
    private final com.farmer.service.EmailService emailService;

    public UserServiceImpl(UserRepository userRepo, com.farmer.service.EmailService emailService) {
        this.userRepo = userRepo;
        this.emailService = emailService;
    }

    @Override
    public User login(String email, String password) {
        String trimmedEmail = email != null ? email.trim() : "";
        String trimmedPassword = password != null ? password.trim() : "";

        logger.info("Login attempt for email: [{}]", trimmedEmail);

        return userRepo.findByEmail(trimmedEmail)
                .map(u -> {
                    if (u.getPassword().equals(trimmedPassword)) {
                        logger.info("Login successful for email: [{}]", trimmedEmail);
                        return u;
                    } else {
                        logger.warn(
                                "Login failed: Incorrect password for email: [{}]. Stored password length: {}, Provided password length: {}",
                                trimmedEmail, u.getPassword().length(), trimmedPassword.length());
                        throw new RuntimeException("Invalid email or password");
                    }
                })
                .orElseThrow(() -> {
                    logger.warn("Login failed: Email not found: [{}]", trimmedEmail);
                    return new RuntimeException("Invalid email or password");
                });
    }

    @Override
    @Transactional
    public User register(RegisterRequest req) {
        logger.info("New registration attempt: email=[{}], name=[{}], role=[{}]",
                req.getEmail(), req.getName(), req.getRole());

        String trimmedEmail = req.getEmail() != null ? req.getEmail().trim() : "";
        String trimmedPassword = req.getPassword() != null ? req.getPassword().trim() : "";

        if (trimmedEmail.isEmpty()) {
            logger.warn("Registration failed: Email is empty");
            throw new RuntimeException("Email is required");
        }

        if (userRepo.existsByEmail(trimmedEmail)) {
            logger.warn("Registration failed: Email already exists: {}", trimmedEmail);
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(trimmedEmail);
        user.setPassword(trimmedPassword);

        String roleStr = (req.getRole() != null) ? req.getRole().toUpperCase().trim() : "";

        if (roleStr.isEmpty()) {
            logger.warn("Registration failed: Role is missing for email: {}", trimmedEmail);
            throw new RuntimeException("Role is required");
        }

        try {
            if (roleStr.startsWith("ROLE_")) {
                roleStr = roleStr.substring(5);
            }
            user.setRole(Role.valueOf(roleStr));
            logger.info("Role assigned: {} for email: {}", user.getRole(), trimmedEmail);
        } catch (IllegalArgumentException e) {
            logger.error("Registration failed: Invalid role [{}] for email: {}", roleStr, trimmedEmail);
            throw new RuntimeException("Invalid role provided: " + req.getRole());
        }

        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());

        User savedUser = userRepo.save(user);
        logger.info("User registered successfully. ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());

        // Send Welcome Email
        emailService.sendWelcomeEmail(savedUser);

        return savedUser;
    }
}
