package com.system.core.application.domain.subject.service;

import com.system.core.application.domain.school.School;
import com.system.core.application.domain.school.service.SchoolService;
import com.system.core.application.domain.subject.Subject;
import com.system.core.application.domain.subject.dto.SubjectRequest;
import com.system.core.application.domain.subject.dto.SubjectResponse;
import com.system.core.application.domain.subject.repository.SubjectRepository;
import com.system.core.application.shared.dto.PageResponse;
import com.system.core.application.shared.exception.AccessDeniedException;
import com.system.core.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final SchoolService schoolService;

    public SubjectServiceImpl(
            SubjectRepository subjectRepository,
            SchoolService schoolService
    ) {
        this.subjectRepository = subjectRepository;
        this.schoolService = schoolService;
    }

    @Override
    @Cacheable(value = "page_subjects", key = "#userId + ':' + #page + ':' + #size")
    public PageResponse<SubjectResponse> findAllResponseBySchool(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);
        Pageable sortedPageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<SubjectResponse> subjectsPage =
                subjectRepository.findAllBySchoolId(school.getId(), sortedPageable)
                .map(s -> new SubjectResponse(s.getId(), s.getName()));
        return PageResponse.from(subjectsPage);
    }

    @Override
    public Subject findById(UUID subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundObjectException("Disciplina não encontrada"));
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_subjects", allEntries = true)
    public Subject save(UUID userId, SubjectRequest request) {
        School school = schoolService.findByUserId(userId);
        Subject subject = new Subject(null, school, request.name());
        subject = subjectRepository.save(subject);
        return subject;
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_subjects", allEntries = true)
    public void update(UUID userId, UUID subjectId, SubjectRequest request) {
        School school = schoolService.findByUserId(userId);
        Subject subject = findById(subjectId);
        checkSubjectBelongsToSchool(school, subject);
        subject.setName(request.name());
        subjectRepository.save(subject);
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_subjects", allEntries = true)
    public void deleteById(UUID userId, UUID subjectId) {
        School school = schoolService.findByUserId(userId);
        Subject subject = findById(subjectId);
        checkSubjectBelongsToSchool(school, subject);
        subjectRepository.deleteById(subject.getId());
    }

    private void checkSubjectBelongsToSchool(School school, Subject subject) {
        if (!school.getId().equals(subject.getSchool().getId())) {
            throw new AccessDeniedException("Não pode alterar o disciplina de outra escola");
        }
    }
}
