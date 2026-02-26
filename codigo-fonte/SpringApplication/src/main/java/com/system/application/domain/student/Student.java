package com.system.application.domain.student;

import com.system.application.domain.legalguardian.LegalGuardian;
import com.system.application.domain.school.School;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "student")
public final class Student implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "grade", length = 20)
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_guardian_id", nullable = false)
    private LegalGuardian legalGuardian;

    public Student() {
    }

    public Student(
            UUID id,
            School school,
            String name,
            LocalDate dateOfBirth,
            String grade,
            LegalGuardian legalGuardian
    ) {
        this.id = id;
        this.school = school;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.grade = grade;
        this.legalGuardian = legalGuardian;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public LegalGuardian getLegalGuardian() {
        return legalGuardian;
    }

    public void setLegalGuardian(LegalGuardian legalGuardian) {
        this.legalGuardian = legalGuardian;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
