package com.system.application.domain.subject.service;

import com.system.application.domain.school.School;
import com.system.application.domain.school.service.SchoolService;
import com.system.application.domain.subject.Subject;
import com.system.application.domain.subject.dto.SubjectRequest;
import com.system.application.domain.subject.dto.SubjectResponse;
import com.system.application.domain.subject.repository.SubjectRepository;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubjectServiceImpl implements SubjectService{
    private final SchoolService schoolService;
    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SchoolService schoolService,
                              SubjectRepository subjectRepository) {
        this.schoolService = schoolService;
        this.subjectRepository = subjectRepository;
    }

    @Override
    @Cacheable(key = "#userId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize", value = "page_subjects")
    public PageResponse<SubjectResponse> findAllBySchool(UUID userId, Pageable pageable) {
        School school = schoolService.findByUser(userId);
        Page<SubjectResponse> subjectsPage = subjectRepository.findAllBySchool_Id(school.getId(), pageable)
                .map(s -> new SubjectResponse(s.getId(), s.getName()));
        return new PageResponse<>(
                subjectsPage.getContent(),
                subjectsPage.getNumber(),
                subjectsPage.getSize(),
                subjectsPage.getTotalPages(),
                subjectsPage.getTotalElements(),
                subjectsPage.hasNext(),
                subjectsPage.hasPrevious()
        );
    }

    @Override
    public SubjectResponse findById(UUID subjectId) {
        return subjectRepository.findById(subjectId)
                .map(s -> new SubjectResponse(s.getId(), s.getName()))
                .orElseThrow(() -> new NotFoundObjectException("Disciplina não encontrada"));
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_subjects", allEntries = true)
    public UUID save(UUID userId, SubjectRequest request) {
        School school = schoolService.findByUser(userId);
        Subject subject = new Subject(null, school, request.name());
        subject = subjectRepository.save(subject);
        return subject.getId();
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_subjects", allEntries = true)
    public UUID update(UUID userId, UUID subjectId, SubjectRequest request) {
        School school = schoolService.findByUser(userId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundObjectException("Disciplina não encontrada"));
        boolean belongTheSameSchool = school.getId().equals(subject.getSchool().getId());
        if (!belongTheSameSchool) {
            throw new AccessDeniedException("Não pode alterar o disciplina de outra escola");
        }
        subject.setName(request.name());
        subject = subjectRepository.save(subject);
        return subject.getId();
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_subjects", allEntries = true)
    public void deleteById(UUID userId, UUID subjectId) {
        School school = schoolService.findByUser(userId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundObjectException("Disciplina não encontrada"));
        boolean belongTheSameSchool = school.getId().equals(subject.getSchool().getId());
        if (!belongTheSameSchool) {
            throw new AccessDeniedException("Não pode alterar o disciplina de outra escola");
        }
        subjectRepository.deleteById(subject.getId());
    }
}
