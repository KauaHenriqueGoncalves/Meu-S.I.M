package com.system.application.domain.role.service;

import com.system.application.domain.role.Role;
import com.system.application.domain.role.repository.RoleRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import org.springframework.stereotype.Service;

@Service
public final class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByIdRole(Long id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Not found Role")
        );
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name.toLowerCase()).orElseThrow(
                () -> new NotFoundObjectException("Not found Role")
        );
    }
}
