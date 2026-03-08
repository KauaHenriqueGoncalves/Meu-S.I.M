package com.system.application.core.schoolpayment.repository;

import com.system.application.core.schoolpayment.SchoolPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolPaymentRepository extends JpaRepository<SchoolPayment, UUID> {
    Optional<SchoolPayment> findBySchoolSubscriptionId(UUID schoolSubcriptionId);
}
