package com.system.application.domain.user.service;

import com.system.application.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public final class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
