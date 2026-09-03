package com.meusim.application.modules.identity.base.user.query;

import com.meusim.application.modules.identity.base.user.User;
import com.meusim.application.modules.identity.base.user.dto.ResponsibleSnapshotDTO;
import com.meusim.application.modules.identity.base.user.repository.UserRepository;
import com.meusim.application.modules.identity.profile.collaborator.Collaborator;
import com.meusim.application.modules.identity.profile.collaborator.repository.CollaboratorRepository;
import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.meusim.application.modules.identity.profile.schooladmin.repository.SchoolAdminRepository;
import com.meusim.application.shared.exception.NotFoundObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class ResponsibleSnapshotQueryImpl implements ResponsibleSnapshotQuery {
    private final static Logger log = LoggerFactory.getLogger(ResponsibleSnapshotQueryImpl.class);
    private final UserRepository userRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final SchoolAdminRepository schoolAdminRepository;

    public ResponsibleSnapshotQueryImpl(UserRepository userRepository,
                                        CollaboratorRepository collaboratorRepository,
                                        SchoolAdminRepository schoolAdminRepository) {
        this.userRepository = userRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.schoolAdminRepository = schoolAdminRepository;
    }

    @Override
    public ResponsibleSnapshotDTO findResponsibleSnapshotByUserId(UUID userId) {
        log.info("Buscando ResponsibleSnapshot pelo userId no banco. [userId={}]", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("ResponsibleSnapshot não encontrado. [userId={}]", userId);
                    return new NotFoundObjectException("Não encontrou o usuário");
                });
        Optional<Collaborator> collaborator = collaboratorRepository.findByUserId(userId);
        if (collaborator.isPresent()) {
            return new ResponsibleSnapshotDTO(
                    collaborator.get().getId(),
                    user.getUsername(),
                    "SCOPE_collaborator"
            );
        }
        Optional<SchoolAdmin> schoolAdmin = schoolAdminRepository.findByUserId(userId);
        if (schoolAdmin.isPresent()) {
            return new ResponsibleSnapshotDTO(
                    schoolAdmin.get().getId(),
                    user.getUsername(),
                    "SCOPE_school_admin"
            );
        }
        log.error("Usuário sem vínculo de collaborator ou school_admin. [userId={}]", userId);
        throw new NotFoundObjectException("Usuário não é colaborador nem administrador escolar");
    }
}
