package com.farmer.controller;

import com.farmer.entity.FarmerDocument;
import com.farmer.entity.VerificationStatus;
import com.farmer.service.VerificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin("*")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/{farmerId}/upload")
    public FarmerDocument upload(@PathVariable Long farmerId,
                                 @RequestParam String aadhaarNumber,
                                 @RequestParam String panNumber,
                                 @RequestParam MultipartFile aadhaarImage,
                                 @RequestParam MultipartFile panImage) throws IOException {
        return verificationService.uploadDocuments(farmerId, aadhaarNumber, panNumber, aadhaarImage, panImage);
    }

    @PutMapping("/{documentId}/status")
    public FarmerDocument updateStatus(@PathVariable Long documentId,
                                       @RequestParam String status,
                                       @RequestParam(required = false) String reason) {
        return verificationService.verifyFarmer(documentId, VerificationStatus.valueOf(status.toUpperCase()), reason);
    }

    @GetMapping("/{farmerId}")
    public FarmerDocument getStatus(@PathVariable Long farmerId) {
        return verificationService.getStatus(farmerId);
    }
}
