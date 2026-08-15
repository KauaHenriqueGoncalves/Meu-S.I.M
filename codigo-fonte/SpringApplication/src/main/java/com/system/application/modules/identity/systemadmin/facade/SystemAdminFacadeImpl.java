package com.system.application.modules.identity.systemadmin.facade;

import com.system.application.modules.identity.systemadmin.SystemAdmin;
import com.system.application.modules.identity.systemadmin.dto.SystemAdminDetailViewResponseDTO;
import com.system.application.modules.identity.systemadmin.dto.SystemAdminSimpleViewResponseDTO;
import com.system.application.modules.identity.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.system.application.modules.identity.systemadmin.service.SystemAdminService;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
public class SystemAdminFacadeImpl implements SystemAdminFacade {
    private final SystemAdminService service;

    public SystemAdminFacadeImpl(SystemAdminService service) {
        this.service = service;
    }

    @Override
    public List<SystemAdmin> getAllEntities() {
        return service.findAll();
    }

    @Override
    public List<SystemAdminSimpleViewResponseDTO> getAll() {
        List<SystemAdmin> admins = service.findAllWithCache();
        return SystemAdminSimpleViewResponseDTO.of(admins);
    }

    @Override
    public SystemAdmin getByIdEntity(UUID id) {
        return service.findById(id);
    }

    @Override
    public SystemAdminDetailViewResponseDTO getById(UUID id) {
        SystemAdmin s = service.findByIdWithCache(id);
        return SystemAdminDetailViewResponseDTO.of(s);
    }

    @Override
    public SystemAdmin getByCpfAndEmailEntity(String cpf, String email) {
        return service.findByCpfAndEmail(cpf, email);
    }

    @Override
    public void create(UserRequest request) {
        service.save(request);
    }

    @Override
    public void update(UUID id, UpdateSystemAdminRequestDTO updateRequest) {
        service.update(id, updateRequest);
    }

    @Override
    public void updatePassword(UUID id, PasswordRequest request) {
        service.updatePassword(id, request);
    }

    @Override
    public void delete(UUID id) {
        service.deleteById(id);
    }
}
