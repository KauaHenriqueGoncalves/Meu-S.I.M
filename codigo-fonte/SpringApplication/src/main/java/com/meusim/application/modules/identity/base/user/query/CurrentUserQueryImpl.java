package com.meusim.application.modules.identity.base.user.query;

import com.meusim.application.auth.service.AuthenticatedUserService;
import com.meusim.application.modules.identity.base.user.dto.CurrentUserDTO;
import com.meusim.application.modules.identity.profile.collaborator.Collaborator;
import com.meusim.application.modules.identity.profile.collaborator.repository.CollaboratorRepository;
import com.meusim.application.modules.identity.profile.legalguardian.LegalGuardian;
import com.meusim.application.modules.identity.profile.legalguardian.repository.LegalGuardianRepository;
import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.meusim.application.modules.identity.profile.schooladmin.repository.SchoolAdminRepository;
import com.meusim.application.shared.exception.NotFoundObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class CurrentUserQueryImpl implements CurrentUserQuery{
    private static final Logger log = LoggerFactory.getLogger(CurrentUserQueryImpl.class);
    private final SchoolAdminRepository schoolAdminRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final LegalGuardianRepository legalGuardianRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public CurrentUserQueryImpl(
            SchoolAdminRepository schoolAdminRepository,
            CollaboratorRepository collaboratorRepository,
            LegalGuardianRepository legalGuardianRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.schoolAdminRepository = schoolAdminRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.legalGuardianRepository = legalGuardianRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    public CurrentUserDTO get() {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando o currentUser. [ownerId={}]", ownerId);
        Optional<SchoolAdmin> schoolAdmin = schoolAdminRepository.findByUserId(ownerId);
        if (schoolAdmin.isPresent()) {
            return new CurrentUserDTO(
                    schoolAdmin.get().getId(),
                    schoolAdmin.get().getUser().getUsername(),
                    schoolAdmin.get().getUser().getEmail()
            );
        }
        Optional<Collaborator> collaborator = collaboratorRepository.findByUserId(ownerId);
        if (collaborator.isPresent()) {
            return new CurrentUserDTO(
                    collaborator.get().getId(),
                    collaborator.get().getUser().getUsername(),
                    collaborator.get().getUser().getEmail()
            );
        }
        Optional<LegalGuardian> legalGuardian = legalGuardianRepository.findByUserId(ownerId);
        if (legalGuardian.isPresent()) {
            return new CurrentUserDTO(
                    legalGuardian.get().getId(),
                    legalGuardian.get().getUser().getUsername(),
                    legalGuardian.get().getUser().getEmail()
            );
        }
        throw new NotFoundObjectException("Não foi encontrado informações do usuário");
    }
}
