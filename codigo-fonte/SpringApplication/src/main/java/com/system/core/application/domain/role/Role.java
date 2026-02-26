package com.system.core.application.domain.role;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "role")
public final class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    public Role() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public enum Values {
        SYSTEM_ADMIN(1L),
        SCHOOL_ADMIN(2L),
        COLLABORATOR(3L),
        LEGAL_GUARDIAN(4l);

        private Long value;

        Values(Long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }
    }
}
