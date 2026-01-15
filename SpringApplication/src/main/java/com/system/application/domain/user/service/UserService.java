package com.system.application.domain.user.service;

import com.system.application.domain.role.Role;
import com.system.application.domain.role.service.RoleService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.repository.UserRepository;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public final class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleService roleService,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAll(pageRequest);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Not found")
        );
    }

    public User saveSchoolAdmin(User user) {
        Boolean someDataIsSame = userRepository.existsConflict(user.getEmail(), user.getCpf(), user.getPhoneNumber());
        if (someDataIsSame) throw new EntityAlreadyExistsException("User already exists");
        Role role = roleService.findByName(Role.Values.SCHOOL_ADMIN.name());
        user.setRole(Set.of(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true); //TODO: por padrão será false
        return userRepository.save(user);
    }
}
