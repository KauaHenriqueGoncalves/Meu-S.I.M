package com.system.application.core.subject.service;

import com.system.application.core.subject.Subject;
import com.system.application.core.subject.dto.SubjectRequest;
import com.system.application.core.subject.dto.SubjectResponse;
import com.system.application.shared.dto.PageResponse;

import java.util.UUID;

public interface SubjectService {
    PageResponse<SubjectResponse> findAllResponseBySchool(UUID userId, int page, int size);
    Subject findById(UUID subjectId);
    Subject save(UUID userId, SubjectRequest request);
    void update(UUID userId, UUID subjectId, SubjectRequest request);
    void deleteById(UUID userId, UUID subjectId);
}
