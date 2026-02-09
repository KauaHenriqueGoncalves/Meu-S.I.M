package com.system.application.domain.role.service;

import com.system.application.domain.role.Role;

public interface RoleService {
    Role findByIdRole(Long id);
    Role findByName(String name);
}
