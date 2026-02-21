package com.system.application.domain.subject.service;

import com.system.application.domain.subject.Subject;
import com.system.application.domain.subject.dto.SubjectRequest;
import com.system.application.domain.subject.dto.SubjectResponse;
import com.system.application.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SubjectService {
    PageResponse<SubjectResponse> findAllBySchool(UUID userId, Pageable pageable);
    SubjectResponse findById(UUID subjectId);
    Subject findByIdEntity(UUID subjectId);
    UUID save(UUID userId, SubjectRequest request);
    UUID update(UUID userId, UUID subjectId, SubjectRequest request);
    void deleteById(UUID userId, UUID subjectId);
}
