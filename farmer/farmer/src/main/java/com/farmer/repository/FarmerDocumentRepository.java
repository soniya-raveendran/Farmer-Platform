package com.farmer.repository;

import com.farmer.entity.FarmerDocument;
import com.farmer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerDocumentRepository extends JpaRepository<FarmerDocument, Long> {
    Optional<FarmerDocument> findByFarmer(User farmer);
}
