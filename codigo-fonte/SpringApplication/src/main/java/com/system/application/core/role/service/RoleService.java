package com.system.application.core.role.service;

import com.system.application.core.role.Role;

public interface RoleService {
    Role findByName(String name);
}
