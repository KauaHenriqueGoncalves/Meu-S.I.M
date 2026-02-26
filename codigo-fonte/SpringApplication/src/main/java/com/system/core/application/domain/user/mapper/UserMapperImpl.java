package com.system.core.application.domain.user.mapper;

import com.system.core.application.domain.user.User;
import com.system.core.application.domain.user.dto.UserRequest;
import org.springframework.stereotype.Component;

@Component
public final class UserMapperImpl implements UserMapper {
    public UserMapperImpl() {}

    @Override
    public User toEntity(UserRequest userRequest) {
        return new User(
                null,
                userRequest.username(),
                userRequest.email(),
                userRequest.password(),
                userRequest.cpf(),
                userRequest.phoneNumber(),
                userRequest.address(),
                null,
                null,
                null
        );
    }
}
