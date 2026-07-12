package com.system.application.modules.academic.classroom.service;

import com.system.application.modules.academic.classroom.Classroom;
import com.system.application.modules.academic.classroom.dto.ClassroomDetailResponse;
import com.system.application.modules.academic.classroom.dto.ClassroomRequest;
import com.system.application.modules.academic.classroom.dto.ClassroomResponse;
import com.system.application.modules.academic.classroom.repository.ClassroomRepository;
import com.system.application.modules.academic.classtype.ClassType;
import com.system.application.modules.academic.classtype.service.ClassTypeService;
import com.system.application.modules.academic.student.Student;
import com.system.application.modules.academic.student.service.StudentService;
import com.system.application.modules.academic.subject.Subject;
import com.system.application.modules.academic.subject.service.SubjectService;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.modules.school.School;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.exception.SubscriptionException;
import com.system.application.shared.services.cache.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClassroomServiceImpl")
public class ClassroomServiceImplTest {
    @Mock private ClassroomRepository classroomRepository;
    @Mock private SchoolSubscriptionService schoolSubscriptionService;
    @Mock private SchoolService schoolService;
    @Mock private ClassTypeService classTypeService;
    @Mock private SubjectService subjectService;
    @Mock private StudentService studentService;
    @Mock private CacheService cacheService;

    @InjectMocks
    private ClassroomServiceImpl classroomService;

    private UUID userId;
    private UUID schoolId;
    private UUID classroomId;
    private UUID studentId;
    private UUID subjectId;

    private School school;
    private School outraEscola;
    private ClassType classTypeGroup;
    private ClassType classTypeIndividual;
    private Subject subject;
    private Student student;
    private Classroom classroom;
    private ClassroomRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        schoolId = UUID.randomUUID();
        classroomId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        subjectId = UUID.randomUUID();

        school      = new School(schoolId, "escola-01", "Escola Teste", "12345678000195");
        outraEscola = new School(UUID.randomUUID(), "outra-escola", "Outra Escola", "98765432000100");

        classTypeGroup = new ClassType();
        classTypeGroup.setId(ClassType.Values.GROUP.getValue());
        classTypeGroup.setName("GROUP");

        classTypeIndividual = new ClassType();
        classTypeIndividual.setId(ClassType.Values.INDIVIDUAL.getValue());
        classTypeIndividual.setName("INDIVIDUAL");

        subject = new Subject(subjectId, school, "Matemática");

        student = new Student(
                studentId, school, "Lucas Souza",
                LocalDate.of(2010, 3, 15), "5º ano", null
        );

        classroom = new Classroom(
                classroomId, school, classTypeGroup, subject,
                "Turma A", 10, "", new ArrayList<>()
        );

        request = new ClassroomRequest(
                ClassType.Values.GROUP.getValue(),
                subjectId,
                10,
                "Turma A",
                ""
        );
    }

    @Nested
    @DisplayName("findById()")
    final class FindById {
        @Test
        @DisplayName("deve retornar a turma quando ID existir")
        void shouldReturnClassroom_whenIdExists() {
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));

            Classroom result = classroomService.findById(classroomId);

            assertThat(result).isEqualTo(classroom);
            verify(classroomRepository).findById(classroomId);
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> classroomService.findById(classroomId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Não encontrou a turma");
        }
    }

    @Nested
    @DisplayName("findAllResponseBySchool()")
    final class FindAllResponseBySchool {
        @Test
        @DisplayName("deve retornar página de turmas da escola")
        void shouldReturnPage_whenSchoolHasClassrooms() {
            Pageable pageable = PageRequest.of(0, 10);

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomRepository.findAllBySchoolId(eq(schoolId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(classroom), pageable, 1));

            PageResponse<ClassroomResponse> result =
                    classroomService.findAllResponseBySchool(userId, 0, 10);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().getFirst().name()).isEqualTo("Turma A");
        }

        @Test
        @DisplayName("deve retornar página vazia quando escola não tiver turmas")
        void shouldReturnEmptyPage_whenSchoolHasNoClassrooms() {
            Pageable pageable = PageRequest.of(0, 10);

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomRepository.findAllBySchoolId(eq(schoolId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(), pageable, 0));

            PageResponse<ClassroomResponse> result =
                    classroomService.findAllResponseBySchool(userId, 0, 10);

            assertThat(result.content()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllResponseByStudentId()")
    final class FindAllResponseByStudentId {
        @Test
        @DisplayName("deve retornar lista de turmas do estudante")
        void shouldReturnClassrooms_whenStudentBelongsToSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(studentService.findById(studentId)).thenReturn(student);
            when(classroomRepository.findAllResponseByStudentId(studentId))
                    .thenReturn(List.of(classroom));

            List<ClassroomResponse> result =
                    classroomService.findAllResponseByStudentId(userId, studentId);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().name()).isEqualTo("Turma A");
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando estudante não pertencer à escola")
        void shouldThrowAccessDenied_whenStudentBelongsToDifferentSchool() {
            Student studentDeOutraEscola = new Student(
                    studentId, outraEscola, "Lucas Souza",
                    LocalDate.of(2010, 3, 15), "5º ano", null
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(studentService.findById(studentId)).thenReturn(studentDeOutraEscola);

            assertThatThrownBy(() ->
                    classroomService.findAllResponseByStudentId(userId, studentId))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("findDetailResponseById()")
    final class FindDetailResponseById {
        @Test
        @DisplayName("deve retornar detalhe da turma quando pertencer à escola")
        void shouldReturnDetail_whenClassroomBelongsToSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));

            ClassroomDetailResponse result =
                    classroomService.findDetailResponseById(userId, classroomId);

            assertThat(result.id()).isEqualTo(classroomId);
            assertThat(result.name()).isEqualTo("Turma A");
            assertThat(result.maxStudents()).isEqualTo(10);
            assertThat(result.classType()).isEqualTo(classTypeGroup);
            assertThat(result.subject().name()).isEqualTo("Matemática");
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando turma não pertencer à escola")
        void shouldThrowAccessDenied_whenClassroomBelongsToDifferentSchool() {
            Classroom classroomDeOutraEscola = new Classroom(
                    classroomId, outraEscola, classTypeGroup, subject,
                    "Turma B", 10, "", new ArrayList<>()
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomRepository.findById(classroomId))
                    .thenReturn(Optional.of(classroomDeOutraEscola));

            assertThatThrownBy(() ->
                    classroomService.findDetailResponseById(userId, classroomId))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("save()")
    final class Save {
        @Test
        @DisplayName("deve cadastrar turma GROUP com sucesso")
        void shouldSaveClassroom_whenGroupType() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classTypeService.findById(ClassType.Values.GROUP.getValue()))
                    .thenReturn(classTypeGroup);
            when(subjectService.findById(subjectId)).thenReturn(subject);
            when(classroomRepository.save(any(Classroom.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Classroom result = classroomService.save(userId, request);

            assertThat(result.getName()).isEqualTo("Turma A");
            assertThat(result.getMaxStudents()).isEqualTo(10);
            assertThat(result.getClassType()).isEqualTo(classTypeGroup);
            verify(classroomRepository).save(any(Classroom.class));
        }

        @Test
        @DisplayName("deve cadastrar turma INDIVIDUAL com maxStudents=1")
        void shouldSaveClassroom_whenIndividualTypeWithOneStudent() {
            ClassroomRequest individualRequest = new ClassroomRequest(
                    ClassType.Values.INDIVIDUAL.getValue(), subjectId, 1, "Turma Individual", ""
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classTypeService.findById(ClassType.Values.INDIVIDUAL.getValue()))
                    .thenReturn(classTypeIndividual);
            when(subjectService.findById(subjectId)).thenReturn(subject);
            when(classroomRepository.save(any(Classroom.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Classroom result = classroomService.save(userId, individualRequest);

            assertThat(result.getMaxStudents()).isEqualTo(1);
            verify(classroomRepository).save(any(Classroom.class));
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando INDIVIDUAL tiver maxStudents > 1")
        void shouldThrow_whenIndividualTypeWithMoreThanOneStudent() {
            ClassroomRequest invalidRequest = new ClassroomRequest(
                    ClassType.Values.INDIVIDUAL.getValue(), subjectId, 2, "Turma Individual", ""
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classTypeService.findById(ClassType.Values.INDIVIDUAL.getValue()))
                    .thenReturn(classTypeIndividual);

            assertThatThrownBy(() -> classroomService.save(userId, invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("individuais");

            verify(classroomRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando disciplina pertencer a outra escola")
        void shouldThrow_whenSubjectBelongsToDifferentSchool() {
            Subject subjectDeOutraEscola = new Subject(subjectId, outraEscola, "Física");

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classTypeService.findById(ClassType.Values.GROUP.getValue()))
                    .thenReturn(classTypeGroup);
            when(subjectService.findById(subjectId)).thenReturn(subjectDeOutraEscola);

            assertThatThrownBy(() -> classroomService.save(userId, request))
                    .isInstanceOf(AccessDeniedException.class);

            verify(classroomRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licenca ativa")
        void shouldThrow_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() -> classroomService.save(userId, request))
                    .isInstanceOf(SubscriptionException.class);

            verify(classroomRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update()")
    final class Update {
        @Test
        @DisplayName("deve atualizar a turma com sucesso")
        void shouldUpdateClassroom_whenValid() {
            ClassroomRequest updateRequest = new ClassroomRequest(
                    ClassType.Values.GROUP.getValue(), subjectId, 15, "Turma B", ""
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
            when(classTypeService.findById(ClassType.Values.GROUP.getValue()))
                    .thenReturn(classTypeGroup);
            when(subjectService.findById(subjectId)).thenReturn(subject);
            when(classroomRepository.save(any(Classroom.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            classroomService.update(userId, classroomId, updateRequest);

            assertThat(classroom.getName()).isEqualTo("Turma B");
            assertThat(classroom.getMaxStudents()).isEqualTo(15);
            verify(classroomRepository).save(classroom);
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando novo maxStudents for menor que estudantes atuais")
        void shouldThrow_whenNewMaxStudentsBelowCurrentCount() {
            Student s1 = new Student(UUID.randomUUID(), school, "Aluno 1", LocalDate.of(2010, 1, 1), "5º", null);
            Student s2 = new Student(UUID.randomUUID(), school, "Aluno 2", LocalDate.of(2010, 1, 1), "5º", null);
            classroom.setStudents(new ArrayList<>(Set.of(s1, s2)));

            ClassroomRequest updateRequest = new ClassroomRequest(
                    ClassType.Values.GROUP.getValue(), subjectId, 1, "Turma B", "" // maxStudents < 2 alunos atuais
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
            when(classTypeService.findById(ClassType.Values.GROUP.getValue()))
                    .thenReturn(classTypeGroup);

            assertThatThrownBy(() -> classroomService.update(userId, classroomId, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("quantidade acima");

            verify(classroomRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando turma não pertencer à escola")
        void shouldThrow_whenClassroomBelongsToDifferentSchool() {
            Classroom classroomDeOutraEscola = new Classroom(
                    classroomId, outraEscola, classTypeGroup, subject, "Turma X", 10, "", new ArrayList<>()
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomRepository.findById(classroomId))
                    .thenReturn(Optional.of(classroomDeOutraEscola));

            assertThatThrownBy(() -> classroomService.update(userId, classroomId, request))
                    .isInstanceOf(AccessDeniedException.class);

            verify(classroomRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("addStudent()")
    final class AddStudent {
        @Test
        @DisplayName("deve adicionar estudante à turma com sucesso")
        void shouldAddStudent_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
            when(studentService.findById(studentId)).thenReturn(student);
            when(classroomRepository.save(any(Classroom.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            classroomService.addStudent(userId, classroomId, studentId);

            assertThat(classroom.getStudents()).contains(student);
            verify(classroomRepository).save(classroom);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando turma estiver cheia")
        void shouldThrow_whenClassroomIsFull() {
            List<Student> estudantes = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                estudantes.add(new Student(UUID.randomUUID(), school, "Aluno " + i,
                        LocalDate.of(2010, 1, 1), "5º", null));
            }
            classroom.setStudents(estudantes); // maxStudents = 10, já cheio

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
            when(studentService.findById(studentId)).thenReturn(student);

            assertThatThrownBy(() ->
                    classroomService.addStudent(userId, classroomId, studentId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("cheia");

            verify(classroomRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando estudante não pertencer à escola")
        void shouldThrow_whenStudentBelongsToDifferentSchool() {
            Student studentDeOutraEscola = new Student(
                    studentId, outraEscola, "Lucas Souza",
                    LocalDate.of(2010, 3, 15), "5º ano", null
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
            when(studentService.findById(studentId)).thenReturn(studentDeOutraEscola);

            assertThatThrownBy(() ->
                    classroomService.addStudent(userId, classroomId, studentId))
                    .isInstanceOf(AccessDeniedException.class);

            verify(classroomRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeStudent()")
    final class RemoveStudent {
        @Test
        @DisplayName("deve remover estudante da turma com sucesso")
        void shouldRemoveStudent_whenValid() {
            classroom.getStudents().add(student);

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
            when(studentService.findById(studentId)).thenReturn(student);
            when(classroomRepository.save(any(Classroom.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            classroomService.removeStudent(userId, classroomId, studentId);

            assertThat(classroom.getStudents()).doesNotContain(student);
            verify(classroomRepository).save(classroom);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando estudante não pertencer à turma")
        void shouldThrow_whenStudentNotInClassroom() {
            // classroom.students está vazio — estudante não está na turma

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
            when(studentService.findById(studentId)).thenReturn(student);

            assertThatThrownBy(() ->
                    classroomService.removeStudent(userId, classroomId, studentId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("não pertencer");

            verify(classroomRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteById()")
    final class DeleteById {
        @Test
        @DisplayName("deve excluir a turma com sucesso quando não tiver estudantes")
        void shouldDelete_whenClassroomIsEmpty() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));

            classroomService.deleteById(userId, classroomId);

            verify(classroomRepository).deleteById(classroomId);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando turma tiver estudantes vinculados")
        void shouldThrow_whenClassroomHasStudents() {
            classroom.getStudents().add(student);

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));

            assertThatThrownBy(() ->
                    classroomService.deleteById(userId, classroomId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("estudantes");

            verify(classroomRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando turma não pertencer à escola")
        void shouldThrow_whenClassroomBelongsToDifferentSchool() {
            Classroom classroomDeOutraEscola = new Classroom(
                    classroomId, outraEscola, classTypeGroup, subject, "Turma X", 10, "", new ArrayList<>()
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomRepository.findById(classroomId))
                    .thenReturn(Optional.of(classroomDeOutraEscola));

            assertThatThrownBy(() ->
                    classroomService.deleteById(userId, classroomId))
                    .isInstanceOf(AccessDeniedException.class);

            verify(classroomRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licenca ativa")
        void shouldThrow_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() ->
                    classroomService.deleteById(userId, classroomId))
                    .isInstanceOf(SubscriptionException.class);

            verify(classroomRepository, never()).deleteById(any());
        }
    }
}
