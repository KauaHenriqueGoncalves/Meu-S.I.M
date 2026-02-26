package com.system.application.domain.classtype;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "class_type")
public final class ClassType implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    public ClassType() {
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
        ClassType classType = (ClassType) o;
        return Objects.equals(id, classType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public enum Values {
        INDIVIDUAL(1L),
        GROUP(2L),
        INTENSIVE_REVIEW(3L),
        HOMEWORK_SUPPORT(4L),
        EXAM_PREPARATION(5L),
        SCHOOL_RECOVERY(6L),
        WORKSHOP(7L),
        ONLINE(8L);

        private Long value;

        Values(Long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }
    }
}
