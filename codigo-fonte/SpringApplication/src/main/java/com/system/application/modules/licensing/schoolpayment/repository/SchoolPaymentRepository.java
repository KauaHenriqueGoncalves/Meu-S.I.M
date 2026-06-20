package com.system.application.modules.licensing.schoolpayment.repository;

import com.system.application.modules.licensing.schoolpayment.SchoolPayment;
import com.system.application.modules.licensing.schoolpayment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolPaymentRepository extends JpaRepository<SchoolPayment, UUID> {
    Optional<SchoolPayment> findBySchoolSubscriptionId(UUID schoolSubscriptionId);

    @Query("SELECT sp FROM SchoolPayment sp WHERE sp.status = :status AND sp.createdAt < :expiresAt")
    List<SchoolPayment> findExpiredPendingPayments(
            @Param("status") PaymentStatus status,
            @Param("expiresAt") Instant expiresAt
    );
}
