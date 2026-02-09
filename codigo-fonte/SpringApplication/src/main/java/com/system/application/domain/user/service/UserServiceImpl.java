package com.system.application.domain.user.service;

import com.system.application.domain.role.Role;
import com.system.application.domain.role.service.RoleService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.repository.UserRepository;
import com.system.application.shared.email.service.EmailSendService;
import com.system.application.shared.email.service.EmailVerificationService;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final EmailVerificationService emailVerificationService;
    private final EmailSendService emailSendService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           EmailVerificationService emailVerificationService,
                           EmailSendService emailSendService,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.emailVerificationService = emailVerificationService;
        this.emailSendService = emailSendService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> findAll(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAll(pageRequest);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Not found")
        );
    }

    @Override
    public User findForLogin(String email, String schoolCode) {
        return userRepository.findForLogin(email, schoolCode).orElseThrow(
                () -> new BadCredentialsException("Bad credentials")
        );
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
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
        user.setActive(true); // TODO: Padrão é falso
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

    private void handleExistingUser(User user) {
        if (user.getActive()) {
            throw new EntityAlreadyExistsException("User already exists");
        }
        sendVerificationEmail(user); // Usuário existe mas não confirmou
        throw new BusinessException(
                "Cadastro já iniciado. Reenviamos o e-mail de confirmação."
        );
    }

    private void sendVerificationEmail(User user) {
        String token = emailVerificationService.createToken(user);
        String link = "http://localhost:8080/auth/verify?token=" + token;
        emailSendService.sendConfirmAccountEmail(
                user.getEmail(),
                user.getUsername(),
                link
        );
    }
}
