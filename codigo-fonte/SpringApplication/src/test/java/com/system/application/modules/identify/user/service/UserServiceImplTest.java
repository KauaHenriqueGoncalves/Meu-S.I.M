package com.system.application.modules.identify.user.service;

import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.role.service.RoleService;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.modules.identity.user.event.UserRegisteredEvent;

import com.system.application.modules.identity.user.repository.UserRepository;
import com.system.application.modules.identity.user.service.UserServiceImpl;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User activeUser;
    private User inactiveUser;
    private UserRequest validRequest;
    private Role role;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        role = new Role();
        role.setId(2L);
        role.setName(Role.Values.SCHOOL_ADMIN.name());

        activeUser = new User(
                userId,
                "João Silva",
                "joao@email.com",
                "hashed_password",
                "12345678901",
                "81999990000",
                "Rua A, 100",
                true,
                null,
                null
        );

        inactiveUser = new User(
                userId,
                "João Silva",
                "joao@email.com",
                "hashed_password",
                "12345678901",
                "81999990000",
                "Rua A, 100",
                false,
                null,
                null
        );

        validRequest = new UserRequest(
                "João Silva",
                "joao@email.com",
                "senha123",
                "12345678901",
                "81999990000",
                "Rua A, 100"
        );
    }

    @Nested
    @DisplayName("findById()")
    final class FindById {
        @Test
        @DisplayName("deve retornar usuário quando ID existir")
        void shouldReturnUser_whenIdExists() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));

            User result = userService.findById(userId);

            assertThat(result).isEqualTo(activeUser);
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.findById(userId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Não encontrou o usuário");
        }
    }

    @Nested
    @DisplayName("findUserForLogin()")
    final class FindUserForLogin {
        @Test
        @DisplayName("deve retornar usuário quando credenciais forem válidas")
        void shouldReturnUser_whenCredentialsAreValid() {
            when(userRepository.findForLogin("joao@email.com", "escola-01"))
                    .thenReturn(Optional.of(activeUser));

            User result = userService.findUserForLogin("joao@email.com", "escola-01");

            assertThat(result).isEqualTo(activeUser);
            verify(userRepository).findForLogin("joao@email.com", "escola-01");
        }

        @Test
        @DisplayName("deve lançar BadCredentialsException quando usuário não for encontrado")
        void shouldThrowBadCredentials_whenUserNotFound() {
            when(userRepository.findForLogin(any(), any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findUserForLogin("joao@email.com", "escola-01"))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Credenciais incorretas");
        }
    }

    @Nested
    @DisplayName("registerUserWithRole()")
    final class RegisterUserWithRole {
        @Test
        @DisplayName("deve cadastrar usuário com isActive=false e publicar evento")
        void shouldRegisterUser_withInactiveStateAndPublishEvent() {
            when(userRepository.findByCpf(validRequest.cpf())).thenReturn(Optional.empty());
            when(userRepository.existsByEmail(validRequest.email())).thenReturn(false);
            when(userRepository.existsByCpf(validRequest.cpf())).thenReturn(false);
            when(userRepository.existsByPhoneNumber(validRequest.phoneNumber())).thenReturn(false);
            when(roleService.findByName(Role.Values.SCHOOL_ADMIN.name())).thenReturn(role);
            when(passwordEncoder.encode(validRequest.password())).thenReturn("hashed_password");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.registerUserWithRole(validRequest, Role.Values.SCHOOL_ADMIN);

            assertThat(result.getActive()).isFalse();
            assertThat(result.getPassword()).isEqualTo("hashed_password");
            assertThat(result.getEmail()).isEqualTo("joao@email.com");

            ArgumentCaptor<UserRegisteredEvent> eventCaptor =
                    ArgumentCaptor.forClass(UserRegisteredEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue()).isNotNull();

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("deve lançar EntityAlreadyExistsException quando CPF já estiver ativo")
        void shouldThrowEntityAlreadyExists_whenCpfAlreadyActive() {
            when(userRepository.findByCpf(validRequest.cpf()))
                    .thenReturn(Optional.of(activeUser));

            assertThatThrownBy(() ->
                    userService.registerUserWithRole(validRequest, Role.Values.SCHOOL_ADMIN))
                    .isInstanceOf(EntityAlreadyExistsException.class);

            verifyNoInteractions(eventPublisher);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve reenviar evento e lançar BusinessException quando CPF existir mas pendente")
        void shouldResendEventAndThrowBusiness_whenCpfExistsButInactive() {
            when(userRepository.findByCpf(validRequest.cpf()))
                    .thenReturn(Optional.of(inactiveUser));

            assertThatThrownBy(() ->
                    userService.registerUserWithRole(validRequest, Role.Values.SCHOOL_ADMIN))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("e-mail de confirmação");

            verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar EntityAlreadyExistsException quando e-mail já estiver cadastrado")
        void shouldThrowEntityAlreadyExists_whenEmailConflictExists() {
            when(userRepository.findByCpf(validRequest.cpf())).thenReturn(Optional.empty());
            when(userRepository.existsByEmail(validRequest.email())).thenReturn(true);

            assertThatThrownBy(() ->
                    userService.registerUserWithRole(validRequest, Role.Values.SCHOOL_ADMIN))
                    .isInstanceOf(EntityAlreadyExistsException.class)
                    .hasMessageContaining("E-mail já cadastrado");

            verifyNoInteractions(eventPublisher);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar EntityAlreadyExistsException quando CPF já estiver cadastrado")
        void shouldThrowEntityAlreadyExists_whenCpfConflictExists() {
            when(userRepository.findByCpf(validRequest.cpf())).thenReturn(Optional.empty());
            when(userRepository.existsByEmail(validRequest.email())).thenReturn(false);
            when(userRepository.existsByCpf(validRequest.cpf())).thenReturn(true);

            assertThatThrownBy(() ->
                    userService.registerUserWithRole(validRequest, Role.Values.SCHOOL_ADMIN))
                    .isInstanceOf(EntityAlreadyExistsException.class)
                    .hasMessageContaining("CPF já cadastrado");

            verifyNoInteractions(eventPublisher);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar EntityAlreadyExistsException quando telefone já estiver cadastrado")
        void shouldThrowEntityAlreadyExists_whenPhoneConflictExists() {
            when(userRepository.findByCpf(validRequest.cpf())).thenReturn(Optional.empty());
            when(userRepository.existsByEmail(validRequest.email())).thenReturn(false);
            when(userRepository.existsByCpf(validRequest.cpf())).thenReturn(false);
            when(userRepository.existsByPhoneNumber(validRequest.phoneNumber())).thenReturn(true);

            assertThatThrownBy(() ->
                    userService.registerUserWithRole(validRequest, Role.Values.SCHOOL_ADMIN))
                    .isInstanceOf(EntityAlreadyExistsException.class)
                    .hasMessageContaining("Telefone já cadastrado");

            verifyNoInteractions(eventPublisher);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve buscar a role correta no roleService conforme o enum passado")
        void shouldFetchCorrectRole_basedOnEnumValue() {
            when(userRepository.findByCpf(any())).thenReturn(Optional.empty());
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(userRepository.existsByCpf(any())).thenReturn(false);
            when(userRepository.existsByPhoneNumber(any())).thenReturn(false);
            when(roleService.findByName(Role.Values.COLLABORATOR.name())).thenReturn(role);
            when(passwordEncoder.encode(any())).thenReturn("hashed_password");
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            userService.registerUserWithRole(validRequest, Role.Values.COLLABORATOR);

            verify(roleService).findByName(eq(Role.Values.COLLABORATOR.name()));
        }
    }

    @Nested
    @DisplayName("activateUser()")
    final class ActivateUser {
        @Test
        @DisplayName("deve setar isActive=true e salvar o usuário")
        void shouldSetActiveTrue_andSave() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(inactiveUser));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            userService.activateUser(userId);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getActive()).isTrue();
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando usuário não existir")
        void shouldThrowNotFound_whenUserDoesNotExist() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.activateUser(userId))
                    .isInstanceOf(NotFoundObjectException.class);

            verify(userRepository, never()).save(any());
        }
    }
}