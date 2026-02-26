package com.system.application.domain.subject.service;

import com.system.application.domain.subject.Subject;
import com.system.application.domain.subject.dto.SubjectRequest;
import com.system.application.domain.subject.dto.SubjectResponse;
import com.system.application.shared.dto.PageResponse;

import java.util.UUID;

public interface SubjectService {
    PageResponse<SubjectResponse> findAllResponseBySchool(UUID userId, int page, int size);
    Subject findById(UUID subjectId);
    Subject save(UUID userId, SubjectRequest request);
    void update(UUID userId, UUID subjectId, SubjectRequest request);
    void deleteById(UUID userId, UUID subjectId);
}
