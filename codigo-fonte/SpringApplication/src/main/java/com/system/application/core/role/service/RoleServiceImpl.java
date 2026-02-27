package com.system.application.core.role.service;

import com.system.application.core.role.Role;
import com.system.application.core.role.repository.RoleRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(
            RoleRepository roleRepository
    ) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Cacheable(key = "#name", value = "role_name")
    public Role findByName(String name) {
        return roleRepository.findByName(name.toLowerCase())
                .orElseThrow(() -> new NotFoundObjectException("Not found Role"));
    }
}
