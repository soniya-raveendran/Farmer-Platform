package com.farmer.repository;

import com.farmer.entity.Complaint;
import com.farmer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByRetailer(User retailer);
}
