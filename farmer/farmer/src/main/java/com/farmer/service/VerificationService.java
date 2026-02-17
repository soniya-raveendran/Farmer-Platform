package com.farmer.service;

import com.farmer.entity.FarmerDocument;
import com.farmer.entity.VerificationStatus;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface VerificationService {

    FarmerDocument uploadDocuments(Long farmerId, String aadhaarNum, String panNum, MultipartFile aadhaarImg, MultipartFile panImg) throws IOException;

    FarmerDocument verifyFarmer(Long documentId, VerificationStatus status, String reason);

    FarmerDocument getStatus(Long farmerId);
}
