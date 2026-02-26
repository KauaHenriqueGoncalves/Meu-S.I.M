package com.system.application.domain.user.mapper;

import com.system.application.domain.user.User;
import com.system.application.domain.user.dto.UserRequest;

public interface UserMapper {
    User toEntity(UserRequest userRequest);
}
