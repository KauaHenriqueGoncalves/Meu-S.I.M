package com.system.application.domain.user.service;

import com.system.application.domain.role.Role;
import com.system.application.domain.role.service.RoleService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.repository.UserRepository;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
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
    @Transactional
    public User saveSchoolAdmin(User user) {
        Boolean someDataIsSame = userRepository.existsConflict(user.getEmail(), user.getCpf(), user.getPhoneNumber());
        if (someDataIsSame) throw new EntityAlreadyExistsException("User already exists");
        Role role = roleService.findByName(Role.Values.SCHOOL_ADMIN.name());
        user.setRole(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true); //TODO: por padrão será false
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User saveSystemAdmin(User user) {
        Boolean someDataIsSame = userRepository.existsConflict(user.getEmail(), user.getCpf(), user.getPhoneNumber());
        if (someDataIsSame) throw new EntityAlreadyExistsException("User already exists");
        Role role = roleService.findByName(Role.Values.SYSTEM_ADMIN.name());
        user.setRole(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true); //TODO: por padrão será false
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User saveColaborator(User user) {
        Boolean someDataIsSame = userRepository.existsConflict(user.getEmail(), user.getCpf(), user.getPhoneNumber());
        if (someDataIsSame) throw new EntityAlreadyExistsException("User already exists");
        Role role = roleService.findByName(Role.Values.COLLABORATOR.name());
        user.setRole(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true); //TODO: por padrão será false
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User saveLegalGuardian(User user) {
        Boolean someDataIsSame = userRepository.existsConflict(user.getEmail(), user.getCpf(), user.getPhoneNumber());
        if (someDataIsSame) throw new EntityAlreadyExistsException("User already exists");
        Role role = roleService.findByName(Role.Values.LEGAL_GUARDIAN.name());
        user.setRole(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true); //TODO: por padrão será false
        return userRepository.save(user);
    }
}
