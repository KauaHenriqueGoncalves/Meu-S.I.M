package com.system.application.domain.role.service;

import com.system.application.domain.role.Role;
import com.system.application.domain.role.repository.RoleRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import org.springframework.stereotype.Service;

@Service
public final class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByIdRole(Long id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Not found Role")
        );
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name.toLowerCase()).orElseThrow(
                () -> new NotFoundObjectException("Not found Role")
        );
    }
}
