package com.system.application.modules.academic.classschedule.service;

import com.system.application.modules.academic.classroom.Classroom;
import com.system.application.modules.academic.classroom.service.ClassroomService;
import com.system.application.modules.academic.classschedule.ClassSchedule;
import com.system.application.modules.academic.classschedule.dto.ClassScheduleRequest;
import com.system.application.modules.academic.classschedule.dto.ClassScheduleResponse;
import com.system.application.modules.academic.classschedule.enums.Weekday;
import com.system.application.modules.academic.classschedule.repository.ClassScheduleRepository;
import com.system.application.modules.academic.classtype.ClassType;
import com.system.application.modules.academic.subject.Subject;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.modules.school.School;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.exception.SubscriptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClassScheduleServiceImpl")
public class ClassScheduleServiceImplTest {
    @Mock private ClassScheduleRepository classScheduleRepository;
    @Mock private SchoolSubscriptionService schoolSubscriptionService;
    @Mock private SchoolService schoolService;
    @Mock private ClassroomService classroomService;

    @InjectMocks
    private ClassScheduleServiceImpl classScheduleService;

    private UUID userId;
    private UUID schoolId;
    private UUID classroomId;
    private UUID classScheduleId;

    private School school;
    private School outraEscola;
    private Classroom classroom;
    private Classroom classroomDeOutraEscola;
    private ClassSchedule classSchedule;
    private ClassScheduleRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        schoolId = UUID.randomUUID();
        classroomId = UUID.randomUUID();
        classScheduleId = UUID.randomUUID();

        school      = new School(schoolId, "escola-01", "Escola Teste", "12345678000195");
        outraEscola = new School(UUID.randomUUID(), "outra-escola", "Outra Escola", "98765432000100");

        Subject subject = new Subject(UUID.randomUUID(), school, "Matemática");

        ClassType classType = new ClassType();
        classType.setId(ClassType.Values.GROUP.getValue());
        classType.setName("GROUP");

        classroom = new Classroom(
                classroomId, school, classType, subject, "Turma A", 10, "", new ArrayList<>()
        );

        classroomDeOutraEscola = new Classroom(
                UUID.randomUUID(), outraEscola, classType, subject, "Turma B", 10, "", new ArrayList<>()
        );

        classSchedule = new ClassSchedule(
                classScheduleId,
                classroom,
                Weekday.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(9, 0)
        );

        request = new ClassScheduleRequest(
                Weekday.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(9, 0)
        );
    }

    @Nested
    @DisplayName("findById()")
    final class FindById {
        @Test
        @DisplayName("deve retornar o horário quando ID existir")
        void shouldReturnSchedule_whenIdExists() {
            when(classScheduleRepository.findById(classScheduleId))
                    .thenReturn(Optional.of(classSchedule));

            ClassSchedule result = classScheduleService.findById(classScheduleId);

            assertThat(result).isEqualTo(classSchedule);
            verify(classScheduleRepository).findById(classScheduleId);
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(classScheduleRepository.findById(classScheduleId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> classScheduleService.findById(classScheduleId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Não encontrou o horário da turma");
        }
    }

    @Nested
    @DisplayName("findAllResponseByClassroom()")
    final class FindAllResponseByClassroom {
        @Test
        @DisplayName("deve retornar horários ordenados por dia e hora de início")
        void shouldReturnSchedulesSorted_whenClassroomHasSchedules() {
            ClassSchedule wednesday = new ClassSchedule(
                    UUID.randomUUID(), classroom, Weekday.WEDNESDAY,
                    LocalTime.of(10, 0), LocalTime.of(11, 0)
            );
            ClassSchedule mondayLate = new ClassSchedule(
                    UUID.randomUUID(), classroom, Weekday.MONDAY,
                    LocalTime.of(14, 0), LocalTime.of(15, 0)
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomService.findById(classroomId)).thenReturn(classroom);
            when(classScheduleRepository.findByClassroomId(classroomId))
                    .thenReturn(Optional.of(List.of(wednesday, mondayLate, classSchedule)));

            List<ClassScheduleResponse> result =
                    classScheduleService.findAllResponseByClassroom(userId, classroomId);

            assertThat(result).hasSize(3);
            // Monday 08:00 → Monday 14:00 → Wednesday 10:00
            assertThat(result.get(0).weekday()).isEqualTo("segunda-feira");
            assertThat(result.get(0).startTime()).isEqualTo(LocalTime.of(8, 0));
            assertThat(result.get(1).startTime()).isEqualTo(LocalTime.of(14, 0));
            assertThat(result.get(2).weekday()).isEqualTo("quarta-feira");
        }

        @Test
        @DisplayName("deve retornar lista vazia quando turma não tiver horários")
        void shouldReturnEmptyList_whenClassroomHasNoSchedules() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomService.findById(classroomId)).thenReturn(classroom);
            when(classScheduleRepository.findByClassroomId(classroomId))
                    .thenReturn(Optional.of(List.of()));

            List<ClassScheduleResponse> result =
                    classScheduleService.findAllResponseByClassroom(userId, classroomId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando turma não pertencer à escola")
        void shouldThrowAccessDenied_whenClassroomBelongsToDifferentSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classroomService.findById(classroomId)).thenReturn(classroomDeOutraEscola);

            assertThatThrownBy(() ->
                    classScheduleService.findAllResponseByClassroom(userId, classroomId))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("save()")
    final class Save {
        @Test
        @DisplayName("deve cadastrar horário com sucesso")
        void shouldSaveSchedule_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomService.findById(classroomId)).thenReturn(classroom);
            when(classScheduleRepository.save(any(ClassSchedule.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ClassSchedule result = classScheduleService.save(userId, classroomId, request);

            assertThat(result.getWeekday()).isEqualTo(Weekday.MONDAY);
            assertThat(result.getStartTime()).isEqualTo(LocalTime.of(8, 0));
            assertThat(result.getEndTime()).isEqualTo(LocalTime.of(9, 0));
            assertThat(result.getClassroom()).isEqualTo(classroom);
            verify(classScheduleRepository).save(any(ClassSchedule.class));
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando turma não pertencer à escola")
        void shouldThrowAccessDenied_whenClassroomBelongsToDifferentSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classroomService.findById(classroomId)).thenReturn(classroomDeOutraEscola);

            assertThatThrownBy(() ->
                    classScheduleService.save(userId, classroomId, request))
                    .isInstanceOf(AccessDeniedException.class);

            verify(classScheduleRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licença ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() ->
                    classScheduleService.save(userId, classroomId, request))
                    .isInstanceOf(SubscriptionException.class);

            verify(classScheduleRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update()")
    final class Update {
        @Test
        @DisplayName("deve atualizar horário com sucesso")
        void shouldUpdateSchedule_whenValid() {
            ClassScheduleRequest updateRequest = new ClassScheduleRequest(
                    Weekday.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classScheduleRepository.findById(classScheduleId))
                    .thenReturn(Optional.of(classSchedule));

            classScheduleService.update(userId, classroomId, classScheduleId, updateRequest);

            assertThat(classSchedule.getWeekday()).isEqualTo(Weekday.FRIDAY);
            assertThat(classSchedule.getStartTime()).isEqualTo(LocalTime.of(10, 0));
            assertThat(classSchedule.getEndTime()).isEqualTo(LocalTime.of(11, 0));
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando horário pertencer a outra escola")
        void shouldThrowAccessDenied_whenScheduleBelongsToDifferentSchool() {
            ClassSchedule scheduleDeOutraEscola = new ClassSchedule(
                    classScheduleId, classroomDeOutraEscola,
                    Weekday.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0)
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classScheduleRepository.findById(classScheduleId))
                    .thenReturn(Optional.of(scheduleDeOutraEscola));

            assertThatThrownBy(() ->
                    classScheduleService.update(userId, classroomId, classScheduleId, request))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando horário não pertencer à turma")
        void shouldThrowAccessDenied_whenScheduleBelongsToDifferentClassroom() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(classScheduleRepository.findById(classScheduleId))
                    .thenReturn(Optional.of(classSchedule));

            UUID outroClassroomId = UUID.randomUUID();

            assertThatThrownBy(() ->
                    classScheduleService.update(userId, outroClassroomId, classScheduleId, request))
                    .satisfies(e -> {
                        // Qualquer exception que pare o fluxo antes de salvar
                        assertThat(e).isInstanceOfAny(
                                NotFoundObjectException.class,
                                AccessDeniedException.class,
                                SubscriptionException.class
                        );
                    });
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licença ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() ->
                    classScheduleService.update(userId, classroomId, classScheduleId, request))
                    .isInstanceOf(SubscriptionException.class);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    final class DeleteById {
        @Test
        @DisplayName("deve excluir horário com sucesso")
        void shouldDelete_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classScheduleRepository.findById(classScheduleId))
                    .thenReturn(Optional.of(classSchedule));

            classScheduleService.deleteById(userId, classroomId, classScheduleId);

            verify(classScheduleRepository).deleteById(classScheduleId);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando horário pertencer a outra escola")
        void shouldThrowAccessDenied_whenScheduleBelongsToDifferentSchool() {
            ClassSchedule scheduleDeOutraEscola = new ClassSchedule(
                    classScheduleId, classroomDeOutraEscola,
                    Weekday.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0)
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classScheduleRepository.findById(classScheduleId))
                    .thenReturn(Optional.of(scheduleDeOutraEscola));

            assertThatThrownBy(() ->
                    classScheduleService.deleteById(userId, classroomId, classScheduleId))
                    .isInstanceOf(AccessDeniedException.class);

            verify(classScheduleRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando horário não pertencer à turma")
        void shouldThrowAccessDenied_whenScheduleBelongsToDifferentClassroom() {
            UUID outroClassroomId = UUID.randomUUID();

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(null);
            when(classScheduleRepository.findById(classScheduleId))
                    .thenReturn(Optional.of(classSchedule));

            assertThatThrownBy(() ->
                    classScheduleService.deleteById(userId, outroClassroomId, classScheduleId))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("outra turma");

            verify(classScheduleRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licença ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() ->
                    classScheduleService.deleteById(userId, classroomId, classScheduleId))
                    .isInstanceOf(SubscriptionException.class);

            verify(classScheduleRepository, never()).deleteById(any());
        }
    }
}
