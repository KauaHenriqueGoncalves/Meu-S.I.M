package com.system.application.core.schoolsubscription.repository;

import com.system.application.core.schoolsubscription.SchoolSubscription;
import com.system.application.core.schoolsubscription.enums.SchoolSubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SchoolSubscriptionRepository extends JpaRepository<SchoolSubscription, UUID> {
    boolean existsBySchoolIdAndStatus(UUID schoolId, SchoolSubscriptionStatus status);
    List<SchoolSubscription> findAllByStatusAndEndDateBefore(SchoolSubscriptionStatus status, LocalDate date);
}
