package com.system.application.domain.schoolAdmin;

import com.system.application.domain.school.School;
import com.system.application.domain.user.User;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "school_admin")
public final class SchoolAdmin implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User userId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "school_id", nullable = false)
    private School schoolId;

    public SchoolAdmin() {}

    public SchoolAdmin(UUID id,
                       User userId,
                       School schoolId) {
        this.id = id;
        this.userId = userId;
        this.schoolId = schoolId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public School getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(School schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SchoolAdmin that = (SchoolAdmin) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SchoolAdmin{" +
                "id=" + id +
                ", userId=" + userId +
                ", schoolId=" + schoolId +
                '}';
    }
}
