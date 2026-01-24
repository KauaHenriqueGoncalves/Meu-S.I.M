package com.system.application.domain.user.service;

import com.system.application.domain.user.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {
    Page<User> findAll(Integer page, Integer size);
    User findById(UUID id);
    User findForLogin(String email, String schoolCode);
    User saveSchoolAdmin(User user);
    User saveSystemAdmin(User user);
    User saveColaborator(User user);
    User saveLegalGuardian(User user);
}
