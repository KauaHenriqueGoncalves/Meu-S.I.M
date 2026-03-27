package com.system.application.modules.identity.user.service;

import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.role.service.RoleService;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.event.UserRegisteredEvent;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.modules.identity.user.repository.UserRepository;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

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
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado. [userId={}]", id);
                    return new NotFoundObjectException("Não encontrou o usuário");
                });
    }

    @Override
    public User findUserForLogin(String email, String schoolCode) {
        return userRepository.findForLogin(email, schoolCode)
                .orElseThrow(() -> {
                    log.warn("Tentativa de login com credenciais inválidas. [email={}] [schoolCode={}]",
                            email, schoolCode);
                    return new BadCredentialsException("Credenciais incorretas");
                });
    }

    @Override
    @Transactional
    public User registerUserWithRole(UserRequest request, Role.Values roleValues) {
        log.info("Iniciando cadastro de usuário. [email={}] [cpf={}] [perfil={}]",
                request.email(), request.cpf(), roleValues.name());

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

        log.info("Usuário cadastrado com sucesso, aguardando confirmação de e-mail. [userId={}] [email={}] [perfil={}]",
                user.getId(), user.getEmail(), roleValues.name());

        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId()));

        return user;
    }

    @Override
    @Transactional
    public void activateUser(UUID id) {
        log.info("Ativando usuário. [userId={}]", id);

        User user = findById(id);
        user.setActive(true);
        userRepository.save(user);

        log.info("Usuário ativado com sucesso. [userId={}] [email={}]",
                user.getId(), user.getEmail());
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
            log.warn("Conflito de dados no cadastro de usuário. [email={}] [cpf={}]",
                    request.email(), request.cpf());
            throw new EntityAlreadyExistsException("Usuário já cadastrado");
        }
    }

    private void handleExistingUser(User user) {
        if (user.getActive()) {
            log.warn("Tentativa de cadastro com CPF já ativo no sistema. [userId={}] [cpf={}]",
                    user.getId(), user.getCpf());
            throw new EntityAlreadyExistsException("Usuário já cadastrad");
        }

        log.info("Usuário já cadastrado, mas pendente de confirmação. Reenviando e-mail. [userId={}] [email={}]",
                user.getId(), user.getEmail());

        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId())); // Usuário existe mas não confirmou
        throw new BusinessException("Cadastro já iniciado. Reenviamos o e-mail de confirmação.");
    }
}
