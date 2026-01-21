package com.system.application.domain.collaborator.mapper;

import com.system.application.domain.collaborator.Collaborator;
import com.system.application.domain.collaborator.dto.CollaboratorDetailResponse;
import com.system.application.domain.collaborator.dto.CollaboratorResponse;
import org.springframework.stereotype.Component;

@Component
public class CollaboratorMapperImpl implements CollaboratorMapper {
    public CollaboratorMapperImpl() {}

    @Override
    public CollaboratorResponse toDto(Collaborator collaborator) {
        return new CollaboratorResponse(
                collaborator.getId(),
                collaborator.getUser().getUsername(),
                collaborator.getSpecialty(),
                collaborator.getWorkload()
        );
    }

    @Override
    public CollaboratorDetailResponse toDtoDetail(Collaborator collaborator) {
        return new CollaboratorDetailResponse(
                collaborator.getId(),
                collaborator.getUser().getUsername(),
                collaborator.getUser().getEmail(),
                collaborator.getUser().getCpf(),
                collaborator.getUser().getPhoneNumber(),
                collaborator.getUser().getAddress(),
                collaborator.getDateOfBirth(),
                collaborator.getSpecialty(),
                collaborator.getWorkload()
        );
    }
}
