package com.system.application.domain.user.mapper;

import com.system.application.domain.user.User;
import com.system.application.domain.user.dto.UserDto;
import com.system.application.domain.user.dto.UserRequest;
import com.system.application.shared.mapper.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public final class UserMapper implements BaseMapper<User, UserDto> {
    public UserMapper() {}

    public User toEntity(UserDto userDto) {
        return new User(
                userDto.id(),
                userDto.username(),
                userDto.email(),
                userDto.password(),
                userDto.cpf(),
                userDto.phoneNumber(),
                userDto.isActive(),
                userDto.createdAt(),
                userDto.role()
        );
    }

    public User toEntity(UserRequest userRequest) {
        return new User(
                null,
                userRequest.username(),
                userRequest.email(),
                userRequest.password(),
                userRequest.cpf(),
                userRequest.phoneNumber(),
                null,
                null,
                null
        );
    }

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getCpf(),
                user.getPhoneNumber(),
                user.getActive(),
                user.getCreatedAt(),
                user.getRole()
        );
    }
}
