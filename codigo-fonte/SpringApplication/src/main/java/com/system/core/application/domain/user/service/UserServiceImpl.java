package com.system.core.application.domain.user.service;

import com.system.core.application.domain.role.Role;
import com.system.core.application.domain.role.service.RoleService;
import com.system.core.application.domain.user.User;
import com.system.core.application.domain.user.dto.UserRegisteredEvent;
import com.system.core.application.domain.user.dto.UserRequest;
import com.system.core.application.domain.user.repository.UserRepository;
import com.system.core.application.shared.email.service.EmailSendService;
import com.system.core.application.shared.email.service.EmailVerificationService;
import com.system.core.application.shared.exception.BusinessException;
import com.system.core.application.shared.exception.EntityAlreadyExistsException;
import com.system.core.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final EmailSendService emailSendService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(
            UserRepository userRepository,
            EmailVerificationService emailVerificationService,
            EmailSendService emailSendService,
            RoleService roleService,
            BCryptPasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
        this.emailSendService = emailSendService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou o usuário"));
    }

    @Override
    public User findUserForLogin(String email, String schoolCode) {
        return userRepository.findForLogin(email, schoolCode)
                .orElseThrow(() -> new BadCredentialsException("Credenciais incorretas"));
    }

    @Override
    @Transactional
    public User registerUserWithRole(UserRequest request, Role.Values roleValues) {
        checkUserAlreadyExists(request);
        checkUserConflict(request);
        Role role = roleService.findByName(roleValues.name());
        User user = new User(
                null,
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.cpf(),
                request.phoneNumber(),
                request.address(),
                false,
                Instant.now(),
                Set.of(role)
        );
        user = userRepository.save(user);
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId()));
        return user;
    }

    @Override
    @Transactional
    public User saveSchoolAdmin(User user) {
        Optional<User> existingUser = userRepository.findByCpf(user.getCpf());
        if (existingUser.isPresent()) {
            handleExistingUser(existingUser.get());
        }
        Boolean conflict = userRepository.existsConflict(
                user.getEmail(),
                user.getCpf(),
                user.getPhoneNumber()
        );
        if (conflict) {
            throw new EntityAlreadyExistsException("User already exists");
        }
        Role role = roleService.findByName(Role.Values.SCHOOL_ADMIN.name());
        user.setRole(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(false); // TODO: Padrão é falso
        User savedUser = userRepository.save(user);
        sendVerificationEmail(savedUser);
        return user;
    }

    @Override
    @Transactional
    public User saveSystemAdmin(User user) {
        Optional<User> existingUser = userRepository.findByCpf(user.getCpf());
        if (existingUser.isPresent()) {
            handleExistingUser(existingUser.get());
        }
        Boolean conflict = userRepository.existsConflict(
                user.getEmail(),
                user.getCpf(),
                user.getPhoneNumber()
        );
        if (conflict) {
            throw new EntityAlreadyExistsException("User already exists");
        }
        Role role = roleService.findByName(Role.Values.SYSTEM_ADMIN.name());
        user.setRole(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true); //TODO: por padrão será false
        User savedUser = userRepository.save(user);
        sendVerificationEmail(savedUser);
        return user;
    }

    @Override
    @Transactional
    public User saveCollaborator(User user) {
        Optional<User> existingUser = userRepository.findByCpf(user.getCpf());
        if (existingUser.isPresent()) {
            handleExistingUser(existingUser.get());
        }
        Boolean conflict = userRepository.existsConflict(
                user.getEmail(),
                user.getCpf(),
                user.getPhoneNumber()
        );
        if (conflict) {
            throw new EntityAlreadyExistsException("User already exists");
        }
        Role role = roleService.findByName(Role.Values.COLLABORATOR.name());
        user.setRole(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true); //TODO: por padrão será false
        User savedUser = userRepository.save(user);
        sendVerificationEmail(savedUser);
        return user;
    }

    @Override
    @Transactional
    public User saveLegalGuardian(User user) {
        Optional<User> existingUser = userRepository.findByCpf(user.getCpf());
        if (existingUser.isPresent()) {
            handleExistingUser(existingUser.get());
        }
        Boolean conflict = userRepository.existsConflict(
                user.getEmail(),
                user.getCpf(),
                user.getPhoneNumber()
        );
        if (conflict) {
            throw new EntityAlreadyExistsException("User already exists");
        }
        Role role = roleService.findByName(Role.Values.LEGAL_GUARDIAN.name());
        user.setRole(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true); //TODO: por padrão será false
        User savedUser = userRepository.save(user);
        sendVerificationEmail(savedUser);
        return user;
    }

    private void checkUserAlreadyExists(UserRequest request) {
        Optional<User> existingUser = userRepository.findByCpf(request.cpf());
        if (existingUser.isPresent()) {
            handleExistingUser(existingUser.get());
        }
    }

    private void checkUserConflict(UserRequest request) {
        Boolean conflict = userRepository.existsConflict(request.email(), request.cpf(), request.phoneNumber());
        if (conflict) {
            throw new EntityAlreadyExistsException("Usuário já cadastrado");
        }
    }

    private void handleExistingUser(User user) {
        if (user.getActive()) throw new EntityAlreadyExistsException("Usuário já cadastrad");
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId())); // Usuário existe mas não confirmou
        throw new BusinessException("Cadastro já iniciado. Reenviamos o e-mail de confirmação.");
    }

    private void sendVerificationEmail(User user) {
        String token = emailVerificationService.createOrRefreshToken(user.getId());
        String link = "http://localhost:8080/auth/verify?token=" + token;
        emailSendService.sendConfirmAccountEmail(
                user.getEmail(),
                user.getUsername(),
                link
        );
    }
}
