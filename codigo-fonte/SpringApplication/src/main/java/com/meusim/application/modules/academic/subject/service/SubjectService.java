package com.meusim.application.modules.academic.subject.service;

import com.meusim.application.modules.academic.subject.Subject;
import com.meusim.application.modules.academic.subject.dto.SubjectRequest;
import com.meusim.application.modules.academic.subject.dto.SubjectResponse;
import com.meusim.application.shared.dto.PageResponse;

import java.util.UUID;

public interface SubjectService {
    PageResponse<SubjectResponse> findAllResponseBySchool(UUID userId, int page, int size);
    Subject findById(UUID subjectId);
    Subject save(UUID userId, SubjectRequest request);
    void update(UUID userId, UUID subjectId, SubjectRequest request);
    void deleteById(UUID userId, UUID subjectId);
}
