package com.system.application.modules.identify.collaborator.service;

import com.system.application.modules.identity.collaborator.Collaborator;
import com.system.application.modules.identity.collaborator.dto.CollaboratorDetailResponse;
import com.system.application.modules.identity.collaborator.dto.CollaboratorRequest;
import com.system.application.modules.identity.collaborator.dto.CollaboratorResponse;
import com.system.application.modules.identity.collaborator.dto.UpdateCollaboratorRequest;
import com.system.application.modules.identity.collaborator.repository.CollaboratorRepository;
import com.system.application.modules.identity.collaborator.service.CollaboratorServiceImpl;
import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.modules.identity.user.service.UserService;
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
import com.system.application.shared.services.cache.keys.CacheKeys;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CollaboratorServiceImpl")
public class CollaboratorServiceImplTest {
    @Mock private CollaboratorRepository collaboratorRepository;
    @Mock private SchoolSubscriptionService schoolSubscriptionService;
    @Mock private UserService userService;
    @Mock private SchoolService schoolService;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private CacheService cacheService;

    @InjectMocks
    private CollaboratorServiceImpl collaboratorService;

    private UUID userId;
    private UUID schoolId;
    private UUID collaboratorId;

    private School school;
    private User user;
    private Collaborator collaborator;
    private SchoolSubscription subscription;

    private UserRequest userRequest;
    private CollaboratorRequest collaboratorRequest;
    private UpdateCollaboratorRequest updateRequest;
    private PasswordRequest passwordRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        schoolId = UUID.randomUUID();
        collaboratorId = UUID.randomUUID();

        school = new School(schoolId, "escola-01", "Escola Teste", "12345678000195");

        user = new User(
                UUID.randomUUID(),
                "Carlos Lima",
                "carlos@email.com",
                "hashed_password",
                "52998224725",
                "81999990000",
                "Rua C, 300",
                true,
                null,
                null
        );

        collaborator = new Collaborator(
                collaboratorId,
                user,
                school,
                LocalDate.of(1990, 5, 10),
                "Matemática",
                "8h"
        );

        subscription = new SchoolSubscription(
                UUID.randomUUID(),
                school,
                null,
                12,
                "Plano Básico",
                BigDecimal.valueOf(99.90),
                50,
                10, // maxCollaborators
                20,
                5,
                LocalDate.now(),
                LocalDate.now().plusMonths(12),
                null
        );

        userRequest = new UserRequest(
                "Carlos Lima",
                "carlos@email.com",
                "senha123",
                "52998224725",
                "81999990000",
                "Rua C, 300"
        );

        collaboratorRequest = new CollaboratorRequest(
                LocalDate.of(1990, 5, 10),
                "Matemática",
                "8h"
        );

        updateRequest = new UpdateCollaboratorRequest(
                "Carlos Atualizado",
                "carlos.novo@email.com",
                "81988880000",
                LocalDate.of(1990, 5, 10),
                "Rua D, 400",
                true,
                "Física",
                "12h"
        );

        passwordRequest = new PasswordRequest("novaSenha123");
    }

    @Nested
    @DisplayName("findById()")
    final class FindById {
        @Test
        @DisplayName("deve retornar o colaborador quando ID existir")
        void shouldReturnCollaborator_whenIdExists() {
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.of(collaborator));

            Collaborator result = collaboratorService.findById(collaboratorId);

            assertThat(result).isEqualTo(collaborator);
            verify(collaboratorRepository).findById(collaboratorId);
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> collaboratorService.findById(collaboratorId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Não encontrou colaborador");
        }
    }

    @Nested
    @DisplayName("findResponseDetailById()")
    final class FindResponseDetailById {
        @Test
        @DisplayName("deve retornar o detalhe do colaborador quando ID existir")
        void shouldReturnDetail_whenIdExists() {
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.of(collaborator));

            CollaboratorDetailResponse result =
                    collaboratorService.findResponseDetailById(collaboratorId);

            assertThat(result.id()).isEqualTo(collaboratorId);
            assertThat(result.username()).isEqualTo(user.getUsername());
            assertThat(result.email()).isEqualTo(user.getEmail());
            assertThat(result.cpf()).isEqualTo(user.getCpf());
            assertThat(result.phoneNumber()).isEqualTo(user.getPhoneNumber());
            assertThat(result.address()).isEqualTo(user.getAddress());
            assertThat(result.isActive()).isEqualTo(user.getActive());
            assertThat(result.dateOfBirth()).isEqualTo(collaborator.getDateOfBirth());
            assertThat(result.specialty()).isEqualTo(collaborator.getSpecialty());
            assertThat(result.workload()).isEqualTo(collaborator.getWorkload());
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> collaboratorService.findResponseDetailById(collaboratorId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Não encontrou colaborador");
        }
    }

    @Nested
    @DisplayName("findAllResponseBySchool()")
    final class FindAllResponseBySchool {

    }

    @Nested
    @DisplayName("save()")
    final class Save {
        @Test
        @DisplayName("deve cadastrar colaborador com sucesso quando licença suportar")
        void shouldSaveCollaborator_whenSubscriptionSupports() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(collaboratorRepository.countBySchoolId(schoolId)).thenReturn(5L); // abaixo do limite (10)
            when(userService.registerUserWithRole(userRequest, Role.Values.COLLABORATOR))
                    .thenReturn(user);
            when(collaboratorRepository.save(any(Collaborator.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Collaborator result = collaboratorService.save(userId, userRequest, collaboratorRequest);

            assertThat(result.getUser()).isEqualTo(user);
            assertThat(result.getSchool()).isEqualTo(school);
            assertThat(result.getSpecialty()).isEqualTo("Matemática");
            assertThat(result.getWorkload()).isEqualTo("8h");
            assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 10));
            verify(collaboratorRepository).save(any(Collaborator.class));
        }

        @Test
        @DisplayName("deve lançar BusinessException quando limite de colaboradores for atingido")
        void shouldThrowBusiness_whenCollaboratorLimitReached() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(collaboratorRepository.countBySchoolId(schoolId))
                    .thenReturn(10L); // igual ao limite

            assertThatThrownBy(() ->
                    collaboratorService.save(userId, userRequest, collaboratorRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("licença");

            verify(collaboratorRepository, never()).save(any());
            verify(userService, never()).registerUserWithRole(any(), any());
        }
    }

    @Nested
    @DisplayName("update()")
    final class Update {
        @Test
        @DisplayName("deve atualizar os dados do colaborador com sucesso")
        void shouldUpdateCollaborator_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.of(collaborator));
            when(collaboratorRepository.save(any(Collaborator.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            collaboratorService.update(userId, collaboratorId, updateRequest);

            assertThat(collaborator.getUser().getUsername()).isEqualTo("Carlos Atualizado");
            assertThat(collaborator.getUser().getEmail()).isEqualTo("carlos.novo@email.com");
            assertThat(collaborator.getUser().getPhoneNumber()).isEqualTo("81988880000");
            assertThat(collaborator.getUser().getAddress()).isEqualTo("Rua D, 400");
            assertThat(collaborator.getUser().getActive()).isTrue();
            assertThat(collaborator.getSpecialty()).isEqualTo("Física");
            assertThat(collaborator.getWorkload()).isEqualTo("12h");
            assertThat(collaborator.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 10));
            verify(collaboratorRepository).save(collaborator);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando colaborador não pertencer à escola do usuário")
        void shouldThrowAccessDenied_whenCollaboratorBelongsToDifferentSchool() {
            School outraEscola = new School(UUID.randomUUID(), "outra-escola", "Outra Escola", "98765432000100");
            Collaborator collaboratorDeOutraEscola = new Collaborator(
                    collaboratorId, user, outraEscola,
                    LocalDate.of(1990, 5, 10), "Matemática", "8h"
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.of(collaboratorDeOutraEscola));

            assertThatThrownBy(() ->
                    collaboratorService.update(userId, collaboratorId, updateRequest)
            ).isInstanceOf(AccessDeniedException.class);

            verify(collaboratorRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licença ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() ->
                    collaboratorService.update(userId, collaboratorId, updateRequest))
                    .isInstanceOf(SubscriptionException.class);

            verify(collaboratorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updatePassword()")
    final class UpdatePassword {
        @Test
        @DisplayName("deve atualizar a senha do colaborador com sucesso")
        void shouldUpdatePassword_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.of(collaborator));
            when(passwordEncoder.encode("novaSenha123")).thenReturn("nova_hash");
            when(collaboratorRepository.save(any(Collaborator.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            collaboratorService.updatePassword(userId, collaboratorId, passwordRequest);

            assertThat(collaborator.getUser().getPassword()).isEqualTo("nova_hash");
            verify(collaboratorRepository).save(collaborator);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando colaborador não pertencer à escola do usuário")
        void shouldThrowAccessDenied_whenCollaboratorBelongsToDifferentSchool() {
            School outraEscola = new School(UUID.randomUUID(), "outra-escola", "Outra Escola", "98765432000100");
            Collaborator collaboratorDeOutraEscola = new Collaborator(
                    collaboratorId, user, outraEscola,
                    LocalDate.of(1990, 5, 10), "Matemática", "8h"
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.of(collaboratorDeOutraEscola));

            assertThatThrownBy(() ->
                    collaboratorService.updatePassword(userId, collaboratorId, passwordRequest))
                    .isInstanceOf(AccessDeniedException.class);

            verify(collaboratorRepository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    final class DeleteById {
        @Test
        @DisplayName("deve excluir o colaborador com sucesso")
        void shouldDelete_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.of(collaborator));

            collaboratorService.deleteById(userId, collaboratorId);

            verify(collaboratorRepository).deleteById(collaboratorId);
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licença ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() ->
                    collaboratorService.deleteById(userId, collaboratorId))
                    .isInstanceOf(SubscriptionException.class);

            verify(collaboratorRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando colaborador não pertencer à escola do usuário")
        void shouldThrowAccessDenied_whenCollaboratorBelongsToDifferentSchool() {
            School outraEscola = new School(UUID.randomUUID(), "outra-escola", "Outra Escola", "98765432000100");
            Collaborator collaboratorDeOutraEscola = new Collaborator(
                    collaboratorId, user, outraEscola,
                    LocalDate.of(1990, 5, 10), "Matemática", "8h"
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(collaboratorRepository.findById(collaboratorId))
                    .thenReturn(Optional.of(collaboratorDeOutraEscola));

            assertThatThrownBy(() ->
                    collaboratorService.deleteById(userId, collaboratorId))
                    .isInstanceOf(AccessDeniedException.class);

            verify(collaboratorRepository, never()).deleteById(any());
        }
    }
}
