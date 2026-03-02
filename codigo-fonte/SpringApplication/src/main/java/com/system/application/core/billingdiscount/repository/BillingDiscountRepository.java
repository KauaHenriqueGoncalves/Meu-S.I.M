package com.system.application.core.billingdiscount.repository;

import com.system.application.core.billingdiscount.BillingDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillingDiscountRepository extends JpaRepository<BillingDiscount, UUID> {
    Optional<BillingDiscount> findByMonths(Integer months);
    Optional<BillingDiscount> findFirstByMonthsLessThanEqualOrderByMonthsDesc(Integer months);
    Optional<BillingDiscount> findByMonthsAndIdNot(Integer months, UUID id);
}
