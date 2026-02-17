package com.farmer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "farmer_documents")
public class FarmerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User farmer;

    private String aadhaarNumber;
    private String panNumber;

    private String aadhaarImageUrl;
    private String panImageUrl;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status = VerificationStatus.PENDING;

    private String rejectionReason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getFarmer() { return farmer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getAadhaarImageUrl() { return aadhaarImageUrl; }
    public void setAadhaarImageUrl(String aadhaarImageUrl) { this.aadhaarImageUrl = aadhaarImageUrl; }

    public String getPanImageUrl() { return panImageUrl; }
    public void setPanImageUrl(String panImageUrl) { this.panImageUrl = panImageUrl; }

    public VerificationStatus getStatus() { return status; }
    public void setStatus(VerificationStatus status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
