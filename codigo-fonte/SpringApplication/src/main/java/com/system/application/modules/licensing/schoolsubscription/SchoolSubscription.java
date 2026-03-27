package com.system.application.modules.licensing.schoolsubscription;

import com.system.application.modules.school.School;
import com.system.application.modules.licensing.schoolplan.SchoolPlan;
import com.system.application.modules.licensing.schoolsubscription.enums.SubscriptionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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

    @Column(name = "plan_name", length = 50, nullable = false)
    private String planName;

    @Column(name = "plan_price", precision = 6, scale = 2, nullable = false)
    private BigDecimal planPrice;

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents;

    @Column(name = "max_collaborators", nullable = false)
    private Integer maxCollaborators;

    @Column(name = "max_legal_guardian", nullable = false)
    private Integer maxLegalGuardian;

    @Column(name = "max_school_admin", nullable = false)
    private Integer maxSchoolAdmin;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public SchoolSubscription() {
    }

    public SchoolSubscription(
            UUID id,
            School school,
            SchoolPlan schoolPlan,
            Integer months,
            String planName,
            BigDecimal planPrice,
            Integer maxStudents,
            Integer maxCollaborators,
            Integer maxLegalGuardian,
            Integer maxSchoolAdmin,
            LocalDate startDate,
            LocalDate endDate,
            SubscriptionStatus status
    ) {
        this.id = id;
        this.school = school;
        this.schoolPlan = schoolPlan;
        this.months = months;
        this.planName = planName;
        this.planPrice = planPrice;
        this.maxStudents = maxStudents;
        this.maxCollaborators = maxCollaborators;
        this.maxLegalGuardian = maxLegalGuardian;
        this.maxSchoolAdmin = maxSchoolAdmin;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public static SchoolSubscription create(
            School school,
            SchoolPlan schoolPlan,
            int months,
            BigDecimal finalPrice,
            SubscriptionStatus status
    ) {
        LocalDate startDate = LocalDate.now();
        return new SchoolSubscription(
                null,
                school,
                schoolPlan,
                months,
                schoolPlan.getName(),
                finalPrice,
                schoolPlan.getMaxStudents(),
                schoolPlan.getMaxCollaborators(),
                schoolPlan.getMaxLegalGuardian(),
                schoolPlan.getMaxSchoolAdmin(),
                startDate,
                startDate.plusMonths(months),
                status
        );
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

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public BigDecimal getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(BigDecimal planPrice) {
        this.planPrice = planPrice;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Integer getMaxCollaborators() {
        return maxCollaborators;
    }

    public void setMaxCollaborators(Integer maxCollaborators) {
        this.maxCollaborators = maxCollaborators;
    }

    public Integer getMaxLegalGuardian() {
        return maxLegalGuardian;
    }

    public void setMaxLegalGuardian(Integer maxLegalGuardian) {
        this.maxLegalGuardian = maxLegalGuardian;
    }

    public Integer getMaxSchoolAdmin() {
        return maxSchoolAdmin;
    }

    public void setMaxSchoolAdmin(Integer maxSchoolAdmin) {
        this.maxSchoolAdmin = maxSchoolAdmin;
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

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
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
