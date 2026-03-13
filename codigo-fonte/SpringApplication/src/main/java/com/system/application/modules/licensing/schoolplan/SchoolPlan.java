package com.system.application.modules.licensing.schoolplan;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "school_plan")
public final class SchoolPlan implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "monthly_price", precision = 6, scale = 2, nullable = false)
    private BigDecimal monthlyPrice;

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents;

    @Column(name = "max_collaborators", nullable = false)
    private Integer maxCollaborators;

    @Column(name = "max_legal_guardian", nullable = false)
    private Integer maxLegalGuardian;

    @Column(name = "max_school_admin", nullable = false)
    private Integer maxSchoolAdmin;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public SchoolPlan() {
    }

    public SchoolPlan(
            UUID id,
            String name,
            BigDecimal monthlyPrice,
            Integer maxStudents,
            Integer maxCollaborators,
            Integer maxLegalGuardian,
            Integer maxSchoolAdmin,
            Boolean isActive
    ) {
        this.id = id;
        this.name = name;
        this.monthlyPrice = monthlyPrice;
        this.maxStudents = maxStudents;
        this.maxCollaborators = maxCollaborators;
        this.maxLegalGuardian = maxLegalGuardian;
        this.maxSchoolAdmin = maxSchoolAdmin;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SchoolPlan that = (SchoolPlan) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SchoolPlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", monthlyPrice=" + monthlyPrice +
                ", maxStudents=" + maxStudents +
                ", maxCollaborators=" + maxCollaborators +
                ", maxLegalGuardian=" + maxLegalGuardian +
                ", maxSchoolAdmin=" + maxSchoolAdmin +
                ", isActive=" + isActive +
                '}';
    }
}
