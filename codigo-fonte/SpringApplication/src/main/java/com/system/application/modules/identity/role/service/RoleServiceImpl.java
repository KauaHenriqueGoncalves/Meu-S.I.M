package com.system.application.modules.identity.role.service;

import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.role.repository.RoleRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger log =
            LoggerFactory.getLogger(RoleServiceImpl.class);

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
                .orElseThrow(() -> {
                    log.warn("Role não encontrada. [name={}]", name);
                    return new NotFoundObjectException("Not found Role");
                });
    }
}
