package com.system.application.core.school;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "school")
public final class School implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name_code", length = 50, nullable = false, unique = true)
    private String nameCode;

    @Column(name = "school_name", length = 50, nullable = false)
    private String schoolName;

    @Column(name = "cnpj",  length = 14, nullable = false, unique = true)
    private String cnpj;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public School() {
    }

    public School(
            UUID id,
            String nameCode,
            String schoolName,
            String cnpj
    ) {
        this.id = id;
        this.nameCode = nameCode;
        this.schoolName = schoolName;
        this.cnpj = cnpj;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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
        School school = (School) o;
        return Objects.equals(id, school.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "School{" +
                "id=" + id +
                ", nameCode='" + nameCode + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
