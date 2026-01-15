package com.system.application.domain.schoolAdmin.controller;

import com.system.application.domain.school.School;
import com.system.application.domain.school.mapper.SchoolMapper;
import com.system.application.domain.schoolAdmin.dto.CreateSchoolAdminRequest;
import com.system.application.domain.schoolAdmin.service.SchoolAdminService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public final class SchoolAdminController {
    private final UserMapper userMapper;
    private final SchoolMapper schoolMapper;
    private final SchoolAdminService schoolAdminService;

    public SchoolAdminController(UserMapper userMapper,
                                 SchoolMapper schoolMapper,
                                 SchoolAdminService schoolAdminService) {
        this.userMapper = userMapper;
        this.schoolMapper = schoolMapper;
        this.schoolAdminService = schoolAdminService;
    }


    @PostMapping("/school-admin")
    public ResponseEntity<Void> createSchoolAdmin(@RequestBody @Valid CreateSchoolAdminRequest createSchoolAdminRequest) {
        User user = userMapper.toEntity(createSchoolAdminRequest.userRequest());
        School school = schoolMapper.toEntity(createSchoolAdminRequest.schoolRequest());
        UUID idSchoolAdmin = schoolAdminService.createSchoolAdmin(user, school);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(idSchoolAdmin)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

}
