package com.system.core.application.domain.role.service;

import com.system.core.application.domain.role.Role;

public interface RoleService {
    Role findByName(String name);
}
