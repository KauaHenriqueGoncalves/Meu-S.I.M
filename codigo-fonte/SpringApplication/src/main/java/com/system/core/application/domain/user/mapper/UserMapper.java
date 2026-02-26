package com.system.core.application.domain.user.mapper;

import com.system.core.application.domain.user.User;
import com.system.core.application.domain.user.dto.UserRequest;

public interface UserMapper {
    User toEntity(UserRequest userRequest);
}
