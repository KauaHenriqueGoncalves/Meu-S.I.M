package com.meusim.application.modules.licensing.schoolsubscription.repository;

import com.meusim.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.meusim.application.modules.licensing.schoolsubscription.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolSubscriptionRepository extends JpaRepository<SchoolSubscription, UUID> {
    Page<SchoolSubscription> findBySchoolId(UUID schoolId, Pageable pageable);
    List<SchoolSubscription> findAllByStatusAndEndDateBefore(SubscriptionStatus status, LocalDate date);
    Optional<SchoolSubscription> findBySchoolIdAndStatus(UUID schoolId, SubscriptionStatus status);
    boolean existsBySchoolIdAndStatus(UUID schoolId, SubscriptionStatus status);
}
