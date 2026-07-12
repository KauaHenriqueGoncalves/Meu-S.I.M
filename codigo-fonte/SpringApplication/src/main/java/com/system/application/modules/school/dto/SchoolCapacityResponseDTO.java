package com.system.application.modules.school.dto;

public record SchoolCapacityResponseDTO(
    long students,
    long collaborators,
    long legalGuardians,
    long schoolAdmins
) { }
