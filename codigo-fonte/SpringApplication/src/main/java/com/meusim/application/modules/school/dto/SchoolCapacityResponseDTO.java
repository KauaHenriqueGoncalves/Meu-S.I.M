package com.meusim.application.modules.school.dto;

public record SchoolCapacityResponseDTO(
    int students,
    int collaborators,
    int legalGuardians,
    int schoolAdmins
) { }
