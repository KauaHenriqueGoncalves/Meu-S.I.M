package com.system.application.domain.legalGuardian;

import com.system.application.domain.school.School;
import com.system.application.domain.user.User;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "legal_guardian")
public final class LegalGuardian implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "degree_of_kinship", length = 30, nullable = false)
    private String degreeOfKinship;

    public LegalGuardian() {}

    public LegalGuardian(UUID id,
                         User user,
                         School school,
                         String degreeOfKinship) {
        this.id = id;
        this.user = user;
        this.school = school;
        this.degreeOfKinship = degreeOfKinship;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public String getDegreeOfKinship() {
        return degreeOfKinship;
    }

    public void setDegreeOfKinship(String degreeOfKinship) {
        this.degreeOfKinship = degreeOfKinship;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LegalGuardian that = (LegalGuardian) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
