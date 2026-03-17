package com.system.application.modules.academic.subject.service;

import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.modules.school.School;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.modules.academic.subject.Subject;
import com.system.application.modules.academic.subject.dto.SubjectRequest;
import com.system.application.modules.academic.subject.dto.SubjectResponse;
import com.system.application.modules.academic.subject.repository.SubjectRepository;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.exception.SubscriptionException;
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
    private final SchoolSubscriptionService schoolSubscriptionService;
    private final SchoolService schoolService;

    public SubjectServiceImpl(
            SubjectRepository subjectRepository,
            SchoolSubscriptionService schoolSubscriptionService,
            SchoolService schoolService
    ) {
        this.subjectRepository = subjectRepository;
        this.schoolSubscriptionService = schoolSubscriptionService;
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
        ensureSchoolHasActiveSubscription(school.getId());
        Subject subject = new Subject(null, school, request.name());
        return subjectRepository.save(subject);
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_subjects", allEntries = true)
    public void update(UUID userId, UUID subjectId, SubjectRequest request) {
        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());
        Subject subject = findById(subjectId);
        checkSubjectBelongsToSchool(school, subject);
        subject.setName(request.name());
    }

    @Override
    @Transactional
    @CacheEvict(value = "page_subjects", allEntries = true)
    public void deleteById(UUID userId, UUID subjectId) {
        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());
        Subject subject = findById(subjectId);
        checkSubjectBelongsToSchool(school, subject);
        subjectRepository.deleteById(subject.getId());
    }

    private void checkSubjectBelongsToSchool(School school, Subject subject) {
        if (!school.getId().equals(subject.getSchool().getId())) {
            throw new AccessDeniedException("Não pode alterar o disciplina de outra escola");
        }
    }

    private void ensureSchoolHasActiveSubscription(UUID schoolId) {
        try {
            schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId);
        }
        catch (SubscriptionException e) {
            throw new SubscriptionException("A escola não possui licença ativa.");
        }
    }
}
