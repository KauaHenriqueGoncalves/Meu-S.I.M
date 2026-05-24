package com.system.application.modules.academic.student.service;

import com.system.application.modules.academic.student.Student;
import com.system.application.modules.academic.student.dto.StudentDetailResponse;
import com.system.application.modules.academic.student.dto.StudentRequest;
import com.system.application.modules.academic.student.dto.StudentResponse;
import com.system.application.modules.academic.student.dto.UpdateStudentRequest;
import com.system.application.modules.academic.student.repository.StudentRepository;
import com.system.application.modules.identity.legalguardian.LegalGuardian;
import com.system.application.modules.identity.legalguardian.service.LegalGuardianService;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
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
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentServiceImpl")
public class StudentServiceImplTest {
    @Mock private StudentRepository studentRepository;
    @Mock private SchoolSubscriptionService schoolSubscriptionService;
    @Mock private SchoolService schoolService;
    @Mock private LegalGuardianService legalGuardianService;
    @Mock private CacheService cacheService;

    @InjectMocks
    private StudentServiceImpl studentService;

    private UUID userId;
    private UUID schoolId;
    private UUID studentId;
    private UUID legalGuardianId;

    private School school;
    private School outraEscola;
    private User user;
    private LegalGuardian legalGuardian;
    private LegalGuardian legalGuardianDeOutraEscola;
    private Student student;
    private SchoolSubscription subscription;

    private StudentRequest studentRequest;
    private UpdateStudentRequest updateRequest;

    @BeforeEach
    void setUp() {
        userId          = UUID.randomUUID();
        schoolId        = UUID.randomUUID();
        studentId       = UUID.randomUUID();
        legalGuardianId = UUID.randomUUID();

        school      = new School(schoolId, "escola-01", "Escola Teste", "12345678000195");
        outraEscola = new School(UUID.randomUUID(), "outra-escola", "Outra Escola", "98765432000100");

        user = new User(
                UUID.randomUUID(), "Maria Silva", "maria@email.com", "hashed",
                "52998224725", "81999990000", "Rua B, 200", true, null, null
        );

        legalGuardian = new LegalGuardian(legalGuardianId, user, school, "Mãe");
        legalGuardianDeOutraEscola = new LegalGuardian(UUID.randomUUID(), user, outraEscola, "Pai");

        student = new Student(
                studentId, school, "Lucas Souza",
                LocalDate.of(2010, 3, 15), "5º ano", legalGuardian
        );

        subscription = new SchoolSubscription(
                UUID.randomUUID(), school, null, 12, "Plano Básico",
                BigDecimal.valueOf(99.90), 50, 10, 20, 5,
                LocalDate.now(), LocalDate.now().plusMonths(12), null
        );

        studentRequest = new StudentRequest(
                "Lucas Souza",
                LocalDate.of(2010, 3, 15),
                "5º ano",
                legalGuardianId
        );

        updateRequest = new UpdateStudentRequest(
                "Lucas Atualizado",
                LocalDate.of(2010, 3, 15),
                "6º ano",
                legalGuardianId
        );
    }

    @Nested
    @DisplayName("findById()")
    final class FindById {
        @Test
        @DisplayName("deve retornar o estudante quando ID existir")
        void shouldReturnStudent_whenIdExists() {
            when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

            Student result = studentService.findById(studentId);

            assertThat(result).isEqualTo(student);
            verify(studentRepository).findById(studentId);
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.findById(studentId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Não encontrou estudante");
        }
    }

    @Nested
    @DisplayName("findResponseDetailById()")
    final class FindResponseDetailById {
        @Test
        @DisplayName("deve retornar detalhe do estudante quando ID existir")
        void shouldReturnDetail_whenIdExists() {
            when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

            StudentDetailResponse result = studentService.findResponseDetailById(studentId);

            assertThat(result.id()).isEqualTo(studentId);
            assertThat(result.name()).isEqualTo("Lucas Souza");
            assertThat(result.dateOfBirth()).isEqualTo(LocalDate.of(2010, 3, 15));
            assertThat(result.grade()).isEqualTo("5º ano");
            assertThat(result.legalGuardianResponse().id()).isEqualTo(legalGuardianId);
            assertThat(result.legalGuardianResponse().username()).isEqualTo(user.getUsername());
            assertThat(result.legalGuardianResponse().degreeOfKinship()).isEqualTo("Mãe");
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.findResponseDetailById(studentId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Não encontrou estudante");
        }
    }

    @Nested
    @DisplayName("findAllResponseBySchool()")
    final class FindAllResponseBySchool {
        @Test
        @DisplayName("deve retornar página de estudantes da escola")
        void shouldReturnPage_whenSchoolHasStudents() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(studentRepository.findAllBySchoolIdAndName(eq(schoolId), any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(student), pageable, 1));

            PageResponse<StudentResponse> result =
                    studentService.findAllResponseBySchool(userId, "", 0, 10);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().getFirst().name()).isEqualTo("Lucas Souza");
            assertThat(result.content().getFirst().grade()).isEqualTo("5º ano");
        }

        @Test
        @DisplayName("deve retornar página vazia quando escola não tiver estudantes")
        void shouldReturnEmptyPage_whenSchoolHasNoStudents() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(studentRepository.findAllBySchoolIdAndName(eq(schoolId), any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(), pageable, 0));

            PageResponse<StudentResponse> result =
                    studentService.findAllResponseBySchool(userId, "", 0, 10);

            assertThat(result.content()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllResponseByLegalGuardian()")
    final class FindAllResponseByLegalGuardian {
        @Test
        @DisplayName("deve retornar lista de estudantes do responsável")
        void shouldReturnStudents_whenLegalGuardianBelongsToSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(legalGuardianService.findById(legalGuardianId)).thenReturn(legalGuardian);
            when(studentRepository.findAllByLegalGuardianId(legalGuardianId))
                    .thenReturn(List.of(student));

            List<StudentResponse> result =
                    studentService.findAllResponseByLegalGuardian(userId, legalGuardianId);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().name()).isEqualTo("Lucas Souza");
        }

        @Test
        @DisplayName("deve retornar lista vazia quando responsável não tiver estudantes")
        void shouldReturnEmptyList_whenLegalGuardianHasNoStudents() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(legalGuardianService.findById(legalGuardianId)).thenReturn(legalGuardian);
            when(studentRepository.findAllByLegalGuardianId(legalGuardianId))
                    .thenReturn(List.of());

            List<StudentResponse> result =
                    studentService.findAllResponseByLegalGuardian(userId, legalGuardianId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando responsável não pertencer à escola")
        void shouldThrowAccessDenied_whenLegalGuardianBelongsToDifferentSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(legalGuardianService.findById(legalGuardianId))
                    .thenReturn(legalGuardianDeOutraEscola);

            assertThatThrownBy(() ->
                    studentService.findAllResponseByLegalGuardian(userId, legalGuardianId))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("save()")
    final class Save {
        @Test
        @DisplayName("deve cadastrar estudante com sucesso quando licença suportar")
        void shouldSaveStudent_whenSubscriptionSupports() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(studentRepository.countBySchoolId(schoolId)).thenReturn(5L); // abaixo do limite (50)
            when(legalGuardianService.findById(legalGuardianId)).thenReturn(legalGuardian);
            when(studentRepository.save(any(Student.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Student result = studentService.save(userId, studentRequest);

            assertThat(result.getName()).isEqualTo("Lucas Souza");
            assertThat(result.getSchool()).isEqualTo(school);
            assertThat(result.getLegalGuardian()).isEqualTo(legalGuardian);
            assertThat(result.getGrade()).isEqualTo("5º ano");
            verify(studentRepository).save(any(Student.class));
        }

        @Test
        @DisplayName("deve lançar BusinessException quando limite de estudantes for atingido")
        void shouldThrowBusiness_whenStudentLimitReached() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(studentRepository.countBySchoolId(schoolId)).thenReturn(50L); // igual ao limite

            assertThatThrownBy(() -> studentService.save(userId, studentRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("licença");

            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando responsável não pertencer à escola")
        void shouldThrowAccessDenied_whenLegalGuardianBelongsToDifferentSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(studentRepository.countBySchoolId(schoolId)).thenReturn(5L);
            when(legalGuardianService.findById(legalGuardianId))
                    .thenReturn(legalGuardianDeOutraEscola);

            assertThatThrownBy(() -> studentService.save(userId, studentRequest))
                    .isInstanceOf(AccessDeniedException.class);

            verify(studentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update()")
    final class Update {
        @Test
        @DisplayName("deve atualizar os dados do estudante com sucesso")
        void shouldUpdateStudent_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(legalGuardianService.findById(legalGuardianId)).thenReturn(legalGuardian);
            when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

            studentService.update(userId, studentId, updateRequest);

            assertThat(student.getName()).isEqualTo("Lucas Atualizado");
            assertThat(student.getGrade()).isEqualTo("6º ano");
            assertThat(student.getLegalGuardian()).isEqualTo(legalGuardian);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando responsável não pertencer à escola")
        void shouldThrowAccessDenied_whenLegalGuardianBelongsToDifferentSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(legalGuardianService.findById(legalGuardianId))
                    .thenReturn(legalGuardianDeOutraEscola);

            assertThatThrownBy(() -> studentService.update(userId, studentId, updateRequest))
                    .isInstanceOf(AccessDeniedException.class);

            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando estudante não pertencer à escola")
        void shouldThrowAccessDenied_whenStudentBelongsToDifferentSchool() {
            Student studentDeOutraEscola = new Student(
                    studentId, outraEscola, "Lucas Souza",
                    LocalDate.of(2010, 3, 15), "5º ano", legalGuardian
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(legalGuardianService.findById(legalGuardianId)).thenReturn(legalGuardian);
            when(studentRepository.findById(studentId)).thenReturn(Optional.of(studentDeOutraEscola));

            assertThatThrownBy(() -> studentService.update(userId, studentId, updateRequest))
                    .isInstanceOf(AccessDeniedException.class);

            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licença ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() -> studentService.update(userId, studentId, updateRequest))
                    .isInstanceOf(SubscriptionException.class);

            verify(studentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteById()")
    final class DeleteById {
        @Test
        @DisplayName("deve excluir o estudante com sucesso")
        void shouldDelete_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

            studentService.deleteById(userId, studentId);

            verify(studentRepository).deleteById(studentId);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando estudante não pertencer à escola")
        void shouldThrowAccessDenied_whenStudentBelongsToDifferentSchool() {
            Student studentDeOutraEscola = new Student(
                    studentId, outraEscola, "Lucas Souza",
                    LocalDate.of(2010, 3, 15), "5º ano", legalGuardian
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(studentRepository.findById(studentId)).thenReturn(Optional.of(studentDeOutraEscola));

            assertThatThrownBy(() -> studentService.deleteById(userId, studentId))
                    .isInstanceOf(AccessDeniedException.class);

            verify(studentRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licença ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() -> studentService.deleteById(userId, studentId))
                    .isInstanceOf(SubscriptionException.class);

            verify(studentRepository, never()).deleteById(any());
        }
    }
}
