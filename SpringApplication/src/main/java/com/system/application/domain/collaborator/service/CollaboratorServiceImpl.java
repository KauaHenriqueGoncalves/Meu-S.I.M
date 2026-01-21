package com.system.application.domain.collaborator.service;

import com.system.application.domain.collaborator.Collaborator;
import com.system.application.domain.collaborator.dto.*;
import com.system.application.domain.collaborator.mapper.CollaboratorMapper;
import com.system.application.domain.collaborator.repository.CollaboratorRepository;
import com.system.application.domain.school.School;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.service.SchoolAdminService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsável pelo gerenciamento de colaboradores.
 *
 * Regras principais:
 * - Um colaborador sempre pertence a uma única escola
 * - Apenas administradores da mesma escola podem gerenciar colaboradores
 * - Todas as operações sensíveis validam o vínculo com a instituição
 */
@Service
public class CollaboratorServiceImpl implements CollaboratorService {
    private final UserService userService;
    private final SchoolAdminService schoolAdminService;
    private final CollaboratorMapper collaboratorMapper;
    private final CollaboratorRepository collaboratorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CollaboratorServiceImpl(UserService userService,
                                   SchoolAdminService schoolAdminService,
                                   CollaboratorMapper collaboratorMapper,
                                   CollaboratorRepository collaboratorRepository,
                                   BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.schoolAdminService = schoolAdminService;
        this.collaboratorMapper = collaboratorMapper;
        this.collaboratorRepository = collaboratorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retorna os colaboradores vinculados à escola do administrador informado.
     *
     * @param adminId id do usuário administrador da escola
     * @param pageable informações de paginação
     * @return página de colaboradores da mesma instituição
     */
    @Override
    public Page<CollaboratorResponse> findAllBySchoolAdminId(UUID adminId, Pageable pageable) {
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        UUID schoolId = schoolAdmin.getSchoolId().getId();
        return collaboratorRepository.findAllBySchoolId(schoolId, pageable)
                .map(c -> new CollaboratorResponse(
                        c.getId(),
                        c.getUsername(),
                        c.getSpecialty(),
                        c.getWorkload()
                ));
    }

    @Override
    public CollaboratorDetailResponse findById(UUID id) {
        Collaborator collaborator = collaboratorRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Not found Collaborator!")
        );
        return collaboratorMapper.toDtoDetail(collaborator);
    }

    /**
     * Cria um novo colaborador vinculado à escola do administrador.
     *
     * Fluxo:
     * 1. Cria o usuário do colaborador
     * 2. Recupera a escola do administrador
     * 3. Associa o colaborador à escola
     *
     * @param user usuário base do colaborador
     * @param adminId id do administrador da escola
     * @param collaboratorRequest dados do colaborador
     * @return id do colaborador criado
     */
    @Override
    @Transactional
    public UUID saveCollaborator(User user, UUID adminId, CollaboratorRequest collaboratorRequest) {
        user = userService.saveColaborator(user);
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        School school = schoolAdmin.getSchoolId();
        Collaborator collaborator = new Collaborator(
                null,
                user,
                school,
                collaboratorRequest.dateOfBirth(),
                collaboratorRequest.specialty(),
                collaboratorRequest.workload()
        );
        collaborator = collaboratorRepository.save(collaborator);
        return collaborator.getId();
    }

    /**
     * Atualiza os dados de um colaborador pertencente à mesma escola do administrador.
     *
     * Regras:
     * - O colaborador deve pertencer à mesma instituição do administrador
     * - Caso o email seja alterado, futuramente a conta poderá ser desativada
     *
     * @throws AccessDeniedException se o colaborador não pertencer à escola
     */
    @Override
    @Transactional
    public UUID updateCollaborator(UUID adminId, UUID collaboratorId, UpdateCollaboratorRequest updateCollaboratorRequest) {
        // TODO: Futuramente implementar que a conta fica desativada, caso troque de email
        Collaborator collaborator = validateCollaboratorBelongsToSchool(adminId, collaboratorId);
        collaborator.getUser().setUsername(updateCollaboratorRequest.username());
        collaborator.getUser().setEmail(updateCollaboratorRequest.email());
        collaborator.getUser().setPhoneNumber(updateCollaboratorRequest.phoneNumber());
        collaborator.getUser().setAddress(updateCollaboratorRequest.address());
        collaborator.setDateOfBirth(updateCollaboratorRequest.dateOfBirth());
        collaborator.setSpecialty(updateCollaboratorRequest.specialty());
        collaborator.setWorkload(updateCollaboratorRequest.workload());
        collaborator = collaboratorRepository.save(collaborator);
        return collaborator.getId();
    }

    /**
     * Atualiza a senha de um colaborador.
     * Apenas administradores da mesma escola podem executar esta operação.
     */
    @Override
    @Transactional
    public void updatePassword(UUID adminId, UUID collaboratorId, UpdateCollaboratorPasswordRequest updatePasswordRequest) {
        Collaborator collaborator = validateCollaboratorBelongsToSchool(adminId, collaboratorId);
        collaborator.getUser().setPassword(passwordEncoder.encode(updatePasswordRequest.newPassword()));
    }

    /**
     * Remove um colaborador da instituição do administrador.
     */
    @Override
    @Transactional
    public void deleteById(UUID adminId, UUID collaboratorId) {
        validateCollaboratorBelongsToSchool(adminId, collaboratorId);
        collaboratorRepository.deleteById(collaboratorId);
    }

    // Valida se o colaborador pertence à mesma escola do administrador
    private Collaborator validateCollaboratorBelongsToSchool(UUID adminId, UUID collaboratorId) {
        UUID schoolId = schoolAdminService.findSchoolIdByUserId(adminId);
        Boolean belongsToSchool = collaboratorRepository.existsByIdAndSchool_Id(collaboratorId, schoolId);
        if (!belongsToSchool) {
            throw new AccessDeniedException("Não pode alterar colaborador de outra instituição");
        }
        return collaboratorRepository.findById(collaboratorId).orElseThrow(
                () -> new NotFoundObjectException("Not found Collaborator")
        );
    }
}
