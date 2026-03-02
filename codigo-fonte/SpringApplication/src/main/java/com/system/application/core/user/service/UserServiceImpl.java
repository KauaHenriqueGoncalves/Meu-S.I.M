package com.system.application.core.user.service;

import com.system.application.core.role.Role;
import com.system.application.core.role.service.RoleService;
import com.system.application.core.user.User;
import com.system.application.core.user.event.UserRegisteredEvent;
import com.system.application.core.user.dto.UserRequest;
import com.system.application.core.user.repository.UserRepository;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
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
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleService roleService,
            BCryptPasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
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
    public void activateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou o usuário"));
        user.setActive(true);
        userRepository.save(user);
    }

    private void checkUserAlreadyExists(UserRequest request) {
        Optional<User> existingUser = userRepository.findByCpf(request.cpf());
        if (existingUser.isPresent()) {
            handleExistingUser(existingUser.get());
        }
    }

    private void checkUserConflict(UserRequest request) {
        boolean conflict = userRepository.existsConflict(request.email(), request.cpf(), request.phoneNumber());
        if (conflict) {
            throw new EntityAlreadyExistsException("Usuário já cadastrado");
        }
    }

    private void handleExistingUser(User user) {
        if (user.getActive()) throw new EntityAlreadyExistsException("Usuário já cadastrad");
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId())); // Usuário existe mas não confirmou
        throw new BusinessException("Cadastro já iniciado. Reenviamos o e-mail de confirmação.");
    }
}
