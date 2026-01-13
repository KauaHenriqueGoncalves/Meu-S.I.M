package com.system.application.domain.user.mapper;

import com.system.application.domain.user.User;
import com.system.application.domain.user.dto.UserDto;
import com.system.application.shared.mapper.BaseMapper;

public final class UserMapper implements BaseMapper<User, UserDto> {
    public UserMapper() {}

    public static UserMapper getInstance() {
        return new UserMapper();
    }

    public User toEntity(UserDto userDto) {
        return null;
    }

    public UserDto toDto(User user) {
        return null;
    }
}
