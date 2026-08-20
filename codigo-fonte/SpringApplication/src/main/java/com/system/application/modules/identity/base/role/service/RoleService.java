package com.system.application.modules.identity.base.role.service;

import com.system.application.modules.identity.base.role.Role;

public interface RoleService {
    Role findByName(String name);
}
