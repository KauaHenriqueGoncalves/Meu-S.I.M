package com.system.application.core.schoolsubscription;

import com.system.application.core.school.School;
import com.system.application.core.schoolplan.SchoolPlan;
import com.system.application.core.schoolsubscription.enums.SchoolSubscriptionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "school_subscription")
public final class SchoolSubscription implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_plan_id", nullable = false)
    private SchoolPlan schoolPlan;

    @Column(name = "months", nullable = false)
    private Integer months;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SchoolSubscriptionStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public SchoolSubscription() {
    }

    public SchoolSubscription(
            UUID id,
            School school,
            SchoolPlan schoolPlan,
            Integer months,
            LocalDate startDate,
            LocalDate endDate,
            SchoolSubscriptionStatus status
    ) {
        this.id = id;
        this.school = school;
        this.schoolPlan = schoolPlan;
        this.months = months;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public SchoolPlan getSchoolPlan() {
        return schoolPlan;
    }

    public void setSchoolPlan(SchoolPlan schoolPlan) {
        this.schoolPlan = schoolPlan;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SchoolSubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SchoolSubscriptionStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SchoolSubscription that = (SchoolSubscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
