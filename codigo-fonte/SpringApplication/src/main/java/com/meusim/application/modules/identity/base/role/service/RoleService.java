package com.meusim.application.modules.identity.base.role.service;

import com.meusim.application.modules.identity.base.role.Role;

public interface RoleService {
    Role findByName(String name);
}
