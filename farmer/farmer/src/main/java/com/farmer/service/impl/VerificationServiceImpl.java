package com.farmer.service.impl;

import com.farmer.entity.FarmerDocument;
import com.farmer.entity.User;
import com.farmer.entity.VerificationStatus;
import com.farmer.repository.FarmerDocumentRepository;
import com.farmer.repository.UserRepository;
import com.farmer.service.CloudinaryService;
import com.farmer.service.NotificationService;
import com.farmer.service.VerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class VerificationServiceImpl implements VerificationService {

    private final FarmerDocumentRepository docRepo;
    private final UserRepository userRepo;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;
    private final com.farmer.service.EmailService emailService;

    public VerificationServiceImpl(FarmerDocumentRepository docRepo, UserRepository userRepo,
            CloudinaryService cloudinaryService, NotificationService notificationService,
            com.farmer.service.EmailService emailService) {
        this.docRepo = docRepo;
        this.userRepo = userRepo;
        this.cloudinaryService = cloudinaryService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public FarmerDocument uploadDocuments(Long farmerId, String aadhaarNum, String panNum, MultipartFile aadhaarImg,
            MultipartFile panImg) throws IOException {
        User farmer = userRepo.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));

        FarmerDocument doc = docRepo.findByFarmer(farmer).orElse(new FarmerDocument());
        doc.setFarmer(farmer);
        doc.setAadhaarNumber(aadhaarNum);
        doc.setPanNumber(panNum);

        // Upload images
        String aadhaarUrl = cloudinaryService.uploadFile(aadhaarImg);
        String panUrl = cloudinaryService.uploadFile(panImg);

        doc.setAadhaarImageUrl(aadhaarUrl);
        doc.setPanImageUrl(panUrl);
        doc.setStatus(VerificationStatus.PENDING);

        userRepo.findByRole(com.farmer.entity.Role.ADMIN).forEach(admin -> {
            notificationService.notify("New Verification Request from " + farmer.getName(), "ADMIN", admin.getId());
        });

        return docRepo.save(doc);
    }

    @Override
    public FarmerDocument verifyFarmer(Long documentId, VerificationStatus status, String reason) {
        FarmerDocument doc = docRepo.findById(documentId).orElseThrow();
        doc.setStatus(status);
        if (status == VerificationStatus.REJECTED) {
            doc.setRejectionReason(reason);
        }

        FarmerDocument saved = docRepo.save(doc);

        notificationService.notify("Your verification status: " + status, "FARMER", doc.getFarmer().getId());

        // Send Email Notification
        emailService.sendVerificationStatusEmail(saved);

        return saved;
    }

    @Override
    public FarmerDocument getStatus(Long farmerId) {
        User farmer = userRepo.findById(farmerId).orElseThrow();
        return docRepo.findByFarmer(farmer).orElse(null);
    }
}
