package com.system.application.modules.academic.classroom;

import com.system.application.modules.academic.classtype.ClassType;
import com.system.application.modules.school.School;
import com.system.application.modules.academic.student.Student;
import com.system.application.modules.academic.subject.Subject;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "classroom")
public final class Classroom implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne
    @JoinColumn(name = "class_type_id")
    private ClassType classType;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Column(name = "max_students")
    private Integer maxStudents;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "classroom_student",
            joinColumns = @JoinColumn(name = "classroom_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students = new HashSet<>();

    public Classroom() {
    }

    public Classroom(
            UUID id,
            School school,
            ClassType classType,
            Subject subject,
            String name,
            Integer maxStudents,
            Set<Student> students
    ) {
        this.id = id;
        this.school = school;
        this.classType = classType;
        this.subject = subject;
        this.name = name;
        this.maxStudents = maxStudents;
        this.students = students;
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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
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

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return Objects.equals(id, classroom.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", school=" + school +
                ", classType=" + classType +
                ", subject=" + subject +
                ", name='" + name + '\'' +
                ", maxStudents=" + maxStudents +
                ", students=" + students +
                '}';
    }
}
