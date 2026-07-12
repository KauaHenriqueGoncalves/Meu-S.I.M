package com.system.application.modules.identity.role.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.role.repository.RoleRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.services.cache.CacheService;
import com.system.application.shared.services.cache.keys.CacheKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger log =
            LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;
    private final CacheService cacheService;

    private static final Duration STUDENT_TTL = Duration.ofHours(20);

    public RoleServiceImpl(
            RoleRepository roleRepository,
            CacheService cacheService
    ) {
        this.roleRepository = roleRepository;
        this.cacheService = cacheService;
    }

    @Override
    public Role findByName(String name) {
        String key = CacheKeys.role(name.toLowerCase());

        log.info("Buscar Role do usuário. [name={}]", name);

        return cacheService.get(key, new TypeReference<Role>(){})
                .orElseGet(() -> {
                    Role role = roleRepository.findByName(name.toLowerCase())
                            .orElseThrow(() -> {
                                log.warn("Role não encontrada. [name={}]", name);
                                return new NotFoundObjectException("Not found Role");
                            });
                    cacheService.set(key, role, STUDENT_TTL);
                    return role;
                });
    }
}
