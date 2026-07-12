package com.system.application.modules.academic.classroom;

import com.system.application.modules.academic.classtype.ClassType;
import com.system.application.modules.school.School;
import com.system.application.modules.academic.student.Student;
import com.system.application.modules.academic.subject.Subject;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

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
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne
    @JoinColumn(name = "class_type_id", nullable = false)
    private ClassType classType;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "classroom_student",
            joinColumns = @JoinColumn(name = "classroom_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "student_id", nullable = false)
    )
    private List<Student> students = new ArrayList<>();

    public Classroom() {
    }

    public Classroom(
            UUID id,
            School school,
            ClassType classType,
            Subject subject,
            String name,
            Integer maxStudents,
            String description,
            List<Student> students
    ) {
        this.id = id;
        this.school = school;
        this.classType = classType;
        this.subject = subject;
        this.name = name;
        this.maxStudents = maxStudents;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
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
