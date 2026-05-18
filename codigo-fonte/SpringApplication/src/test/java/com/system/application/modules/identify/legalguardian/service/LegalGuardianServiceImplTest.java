package com.system.application.modules.identify.legalguardian.service;

import com.system.application.modules.identity.legalguardian.LegalGuardian;
import com.system.application.modules.identity.legalguardian.dto.LegalGuardianDetailResponse;
import com.system.application.modules.identity.legalguardian.dto.LegalGuardianRequest;
import com.system.application.modules.identity.legalguardian.dto.LegalGuardianResponse;
import com.system.application.modules.identity.legalguardian.dto.UpdateLegalGuardianRequest;
import com.system.application.modules.identity.legalguardian.repository.LegalGuardianRepository;
import com.system.application.modules.identity.legalguardian.service.LegalGuardianServiceImpl;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LegalGuardianServiceImpl")
public class LegalGuardianServiceImplTest {
    @Mock private LegalGuardianRepository legalGuardianRepository;
    @Mock private SchoolSubscriptionService schoolSubscriptionService;
    @Mock private UserService userService;
    @Mock private SchoolService schoolService;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private CacheService cacheService;

    @InjectMocks
    private LegalGuardianServiceImpl legalGuardianService;

    private UUID userId;
    private UUID schoolId;
    private UUID legalGuardianId;

    private School school;
    private User user;
    private LegalGuardian legalGuardian;
    private SchoolSubscription subscription;

    private UserRequest userRequest;
    private LegalGuardianRequest legalGuardianRequest;
    private UpdateLegalGuardianRequest updateRequest;
    private PasswordRequest passwordRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        schoolId = UUID.randomUUID();
        legalGuardianId = UUID.randomUUID();

        school = new School(schoolId, "escola-01", "Escola Teste", "12345678000195");

        user = new User(
                UUID.randomUUID(),
                "Maria Silva",
                "maria@email.com",
                "hashed_password",
                "52998224725",
                "81999990000",
                "Rua B, 200",
                true,
                null,
                null
        );

        legalGuardian = new LegalGuardian(legalGuardianId, user, school, "Mãe");

        subscription = new SchoolSubscription(
                UUID.randomUUID(),
                school,
                null,
                12,
                "Plano Básico",
                BigDecimal.valueOf(99.90),
                50,
                10,
                20, // maxLegalGuardian
                5,
                LocalDate.now(),
                LocalDate.now().plusMonths(12),
                null
        );

        userRequest = new UserRequest(
                "Maria Silva",
                "maria@email.com",
                "senha123",
                "52998224725",
                "81999990000",
                "Rua B, 200"
        );

        legalGuardianRequest = new LegalGuardianRequest("Mãe");

        updateRequest = new UpdateLegalGuardianRequest(
                "Maria Atualizada",
                "maria.nova@email.com",
                "81988880000",
                "Rua C, 300",
                true,
                "Pai"
        );

        passwordRequest = new PasswordRequest("novaSenha123");
    }

    @Nested
    @DisplayName("findById()")
    final class FindById {
        @Test
        @DisplayName("deve retornar o responsável quando ID existir")
        void shouldReturnLegalGuardian_whenIdExists() {
            when(legalGuardianRepository.findById(legalGuardianId))
                    .thenReturn(Optional.of(legalGuardian));

            LegalGuardian result = legalGuardianService.findById(legalGuardianId);

            assertThat(result).isEqualTo(legalGuardian);
            verify(legalGuardianRepository).findById(legalGuardianId);
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(legalGuardianRepository.findById(legalGuardianId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> legalGuardianService.findById(legalGuardianId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Não encontrou o responsável");
        }
    }

    @Nested
    @DisplayName("findResponseDetailById()")
    final class FindResponseDetailById {
        @Test
        @DisplayName("deve retornar o detalhe do responsável quando ID existir")
        void shouldReturnDetail_whenIdExists() {
            when(legalGuardianRepository.findById(legalGuardianId))
                    .thenReturn(Optional.of(legalGuardian));

            LegalGuardianDetailResponse result =
                    legalGuardianService.findResponseDetailById(legalGuardianId);

            assertThat(result.id()).isEqualTo(legalGuardianId);
            assertThat(result.username()).isEqualTo(user.getUsername());
            assertThat(result.email()).isEqualTo(user.getEmail());
            assertThat(result.cpf()).isEqualTo(user.getCpf());
            assertThat(result.phoneNumber()).isEqualTo(user.getPhoneNumber());
            assertThat(result.address()).isEqualTo(user.getAddress());
            assertThat(result.isActive()).isEqualTo(user.getActive());
            assertThat(result.degreeOfKinship()).isEqualTo(legalGuardian.getDegreeOfKinship());
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(legalGuardianRepository.findById(legalGuardianId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> legalGuardianService.findResponseDetailById(legalGuardianId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Não encontrou o responsável");
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
        @DisplayName("deve cadastrar responsável com sucesso quando licença suportar")
        void shouldSaveLegalGuardian_whenSubscriptionSupports() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(legalGuardianRepository.countBySchoolId(schoolId)).thenReturn(5L); // abaixo do limite (20)
            when(userService.registerUserWithRole(userRequest, Role.Values.LEGAL_GUARDIAN))
                    .thenReturn(user);
            when(legalGuardianRepository.save(any(LegalGuardian.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            LegalGuardian result = legalGuardianService.save(userId, userRequest, legalGuardianRequest);

            assertThat(result.getUser()).isEqualTo(user);
            assertThat(result.getSchool()).isEqualTo(school);
            assertThat(result.getDegreeOfKinship()).isEqualTo("Mãe");
            verify(legalGuardianRepository).save(any(LegalGuardian.class));
        }

        @Test
        @DisplayName("deve lançar BusinessException quando limite de responsáveis for atingido")
        void shouldThrowBusiness_whenLegalGuardianLimitReached() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(legalGuardianRepository.countBySchoolId(schoolId))
                    .thenReturn(20L); // igual ao limite

            assertThatThrownBy(() ->
                    legalGuardianService.save(userId, userRequest, legalGuardianRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("licença");

            verify(legalGuardianRepository, never()).save(any());
            verify(userService, never()).registerUserWithRole(any(), any());
        }
    }

    @Nested
    @DisplayName("update()")
    final class Update {
        @Test
        @DisplayName("deve atualizar os dados do responsável com sucesso")
        void shouldUpdateLegalGuardian_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(legalGuardianRepository.existsByIdAndSchoolId(legalGuardianId, schoolId))
                    .thenReturn(true);
            when(legalGuardianRepository.findById(legalGuardianId))
                    .thenReturn(Optional.of(legalGuardian));
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(legalGuardianRepository.save(any(LegalGuardian.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            legalGuardianService.update(userId, legalGuardianId, updateRequest);

            assertThat(legalGuardian.getUser().getUsername()).isEqualTo("Maria Atualizada");
            assertThat(legalGuardian.getUser().getEmail()).isEqualTo("maria.nova@email.com");
            assertThat(legalGuardian.getUser().getPhoneNumber()).isEqualTo("81988880000");
            assertThat(legalGuardian.getUser().getAddress()).isEqualTo("Rua C, 300");
            assertThat(legalGuardian.getUser().getActive()).isTrue();
            assertThat(legalGuardian.getDegreeOfKinship()).isEqualTo("Pai");
            verify(legalGuardianRepository).save(legalGuardian);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando responsável não pertencer à escola do usuário")
        void shouldThrowAccessDenied_whenLegalGuardianBelongsToDifferentSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(legalGuardianRepository.existsByIdAndSchoolId(legalGuardianId, schoolId))
                    .thenReturn(false);

            assertThatThrownBy(() ->
                    legalGuardianService.update(userId, legalGuardianId, updateRequest)
            ).isInstanceOf(AccessDeniedException.class);

            verify(legalGuardianRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licença ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(legalGuardianRepository.existsByIdAndSchoolId(legalGuardianId, schoolId))
                    .thenReturn(true);
            when(legalGuardianRepository.findById(legalGuardianId))
                    .thenReturn(Optional.of(legalGuardian));
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() ->
                    legalGuardianService.update(userId, legalGuardianId, updateRequest))
                    .isInstanceOf(SubscriptionException.class);

            verify(legalGuardianRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updatePassword()")
    final class UpdatePassword {
        @Test
        @DisplayName("deve atualizar a senha do responsável com sucesso")
        void shouldUpdatePassword_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(legalGuardianRepository.existsByIdAndSchoolId(legalGuardianId, schoolId))
                    .thenReturn(true);
            when(legalGuardianRepository.findById(legalGuardianId))
                    .thenReturn(Optional.of(legalGuardian));
            when(passwordEncoder.encode("novaSenha123")).thenReturn("nova_hash");
            when(legalGuardianRepository.save(any(LegalGuardian.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            legalGuardianService.updatePassword(userId, legalGuardianId, passwordRequest);

            assertThat(legalGuardian.getUser().getPassword()).isEqualTo("nova_hash");
            verify(legalGuardianRepository).save(legalGuardian);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando responsável não pertencer à escola do usuário")
        void shouldThrowAccessDenied_whenLegalGuardianBelongsToDifferentSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(legalGuardianRepository.existsByIdAndSchoolId(legalGuardianId, schoolId))
                    .thenReturn(false);

            assertThatThrownBy(() ->
                    legalGuardianService.updatePassword(userId, legalGuardianId, passwordRequest))
                    .isInstanceOf(AccessDeniedException.class);

            verify(legalGuardianRepository, never()).save(any());
            verifyNoInteractions(passwordEncoder);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    final class DeleteById {
        @Test
        @DisplayName("deve excluir o responsável com sucesso")
        void shouldDelete_whenValid() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(legalGuardianRepository.existsByIdAndSchoolId(legalGuardianId, schoolId))
                    .thenReturn(true);

            legalGuardianService.deleteById(userId, legalGuardianId);

            verify(legalGuardianRepository).deleteById(legalGuardianId);
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver licença ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenThrow(new SubscriptionException("Sem licença ativa"));

            assertThatThrownBy(() ->
                    legalGuardianService.deleteById(userId, legalGuardianId))
                    .isInstanceOf(SubscriptionException.class);

            verify(legalGuardianRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando responsável não pertencer à escola do usuário")
        void shouldThrowAccessDenied_whenLegalGuardianBelongsToDifferentSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .thenReturn(subscription);
            when(legalGuardianRepository.existsByIdAndSchoolId(legalGuardianId, schoolId))
                    .thenReturn(false);

            assertThatThrownBy(() ->
                    legalGuardianService.deleteById(userId, legalGuardianId))
                    .isInstanceOf(AccessDeniedException.class);

            verify(legalGuardianRepository, never()).deleteById(any());
        }
    }
}
