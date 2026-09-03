package com.meusim.application.modules.identity.profile.schooladmin.controller;

import com.meusim.application.integration.captcha.service.CaptchaService;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.meusim.application.modules.identity.profile.schooladmin.dto.CreateNewSchoolRequestDTO;
import com.meusim.application.modules.identity.profile.schooladmin.dto.SchoolAdminDetailViewResponseDTO;
import com.meusim.application.modules.identity.profile.schooladmin.dto.SchoolAdminSimpleViewResponseDTO;
import com.meusim.application.modules.identity.profile.schooladmin.dto.UpdateSchoolAdminRequestDTO;
import com.meusim.application.modules.identity.profile.schooladmin.facade.SchoolAdminFacade;
import com.meusim.application.shared.dto.PageResponse;
import com.meusim.application.shared.exception.AccessDeniedException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/school-admins")
public class SchoolAdminController {
    private final SchoolAdminFacade facade;
    private final CaptchaService captchaService;

    public SchoolAdminController(
            SchoolAdminFacade facade,
            @Qualifier("turnstile") CaptchaService captchaService) {
        this.facade = facade;
        this.captchaService = captchaService;
    }

    @PostMapping("/new-school")
    public ResponseEntity<Void> createNewSchool(@RequestBody @Valid CreateNewSchoolRequestDTO dto) {
        if (!captchaService.validate(dto.captchaRequestDto().token())) {
            throw new AccessDeniedException("Verificação de segurança falhou!");
        }
        SchoolAdmin sa = facade.createNewSchool(dto.createUserDto(), dto.createSchoolDto());
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(sa.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<PageResponse<SchoolAdminSimpleViewResponseDTO>> pageBySchool(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size,
            @RequestParam(value = "name", defaultValue = "") String name) {
        PageResponse<SchoolAdminSimpleViewResponseDTO> response = facade.pageBySchool(name, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<SchoolAdminDetailViewResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(facade.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserRequestDTO dto) {
        facade.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateSchoolAdminRequestDTO dto) {
        facade.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        facade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
