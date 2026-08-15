package com.system.application.modules.identity.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.auth.service.AuthenticatedUserService;
import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.role.service.RoleService;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.cache.UserCacheKeys;
import com.system.application.modules.identity.user.event.UserRegisteredEvent;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.modules.identity.user.repository.UserRepository;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.services.cache.CacheService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AuthenticatedUserService authenticatedUserService;
    private final CacheService cacheService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleService roleService,
            AuthenticatedUserService authenticatedUserService,
            CacheService cacheService,
            BCryptPasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.authenticatedUserService = authenticatedUserService;
        this.cacheService = cacheService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public User findById(UUID id) {
        log.info("Buscando usuario pelo user ID. [id={}]", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado. [userId={}]", id);
                    return new NotFoundObjectException("Não encontrou o usuário");
                });
        log.info("Usuario encontrado pelo ID. [id={}]", id);
        return user;
    }

    @Override
    public User findByOwnerWithCache() {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando owner pelo user ID. [ownerId={}]", ownerId);
        String key = UserCacheKeys.me(ownerId);
        Optional<User> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Owner encontrado com o userID no cache. [ownerId={}]", ownerId);
            return cache.get();
        }
        User user = findById(ownerId);
        log.info("Owner encontrado com o userID e salvo no cache. [ownerId={}]", ownerId);
        cacheService.set(key, user, UserCacheKeys.TTL);
        return user;
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

        log.info("Usuário cadastrado com sucesso, tentativa de envio de e-mail. [userId={}] [email={}] [perfil={}]",
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
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Tentativa de cadastro com Email já cadastrado. [email={}]", request.email());
            throw new EntityAlreadyExistsException("E-mail já cadastrado");
        }

        if (userRepository.existsByCpf(request.cpf())) {
            log.warn("Tentativa de cadastro com CPF já cadastrado. [cpf={}]", request.cpf());
            throw new EntityAlreadyExistsException("CPF já cadastrado");
        }

        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            log.warn("Tentativa de cadastro com Telefone já cadastrado. [phoneNumber={}]", request.phoneNumber());
            throw new EntityAlreadyExistsException("Telefone já cadastrado");
        }
    }

    private void handleExistingUser(User user) {
        if (user.getActive()) {
            log.warn("Tentativa de cadastro com CPF já ativo no sistema. [userId={}] [cpf={}]",
                    user.getId(), user.getCpf());
            throw new EntityAlreadyExistsException("CPF já cadastrado");
        }

        log.info("Usuário já cadastrado, mas pendente de confirmação. Reenviando e-mail. [userId={}] [email={}]",
                user.getId(), user.getEmail());
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId())); // Usuário existe mas não confirmou
        throw new BusinessException("Cadastro já iniciado. Reenviamos o e-mail de confirmação.");
    }
}
