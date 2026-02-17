package com.farmer.controller;

import com.farmer.dto.UpdateProfileRequest;
import com.farmer.entity.User;
import com.farmer.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ✅ Update Farmer / Retailer Profile
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest req
    ) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Allowed updates only
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());

        return ResponseEntity.ok(userRepo.save(user));
    }
}

