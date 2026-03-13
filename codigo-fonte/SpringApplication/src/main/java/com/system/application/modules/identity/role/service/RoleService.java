package com.system.application.modules.identity.role.service;

import com.system.application.modules.identity.role.Role;

public interface RoleService {
    Role findByName(String name);
}
