package com.meusim.application.modules.classdiary.attendance;

import com.meusim.application.modules.classdiary.attendance.dto.CreateAttendanceRequestDTO;
import com.meusim.application.modules.classdiary.attendance.enums.AttendanceStatus;
import com.meusim.application.modules.classdiary.lesson.Lesson;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "attendance",
        indexes = {
                @Index(name = "idx_lesson_student_id", columnList = "lesson_id, student_id_snap")
        }
)
public final class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "student_id_snap", nullable = false)
    private UUID studentId;

    @Column(name = "student_name_snap", nullable = false)
    private String studentName;

    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Column(name = "content", nullable = false, length = 200, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public Attendance() {
    }

    public Attendance(UUID id,
                      Lesson lesson,
                      UUID studentId,
                      String studentName,
                      AttendanceStatus status,
                      String content) {
        this.id = id;
        this.lesson = lesson;
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
        this.content = content;
    }

    public static Attendance createInit(Lesson entity, CreateAttendanceRequestDTO dto) {
        return new Attendance(
                null,
                entity,
                dto.studentId(),
                dto.studentName(),
                dto.status(),
                dto.content()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        Attendance that = (Attendance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id=" + id +
                ", lesson=" + lesson +
                ", studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", status=" + status +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
