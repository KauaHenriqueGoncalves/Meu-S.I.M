package com.system.application.modules.academic.subject.service;

import com.fasterxml.jackson.core.type.TypeReference;
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
import com.system.application.shared.services.cache.CacheService;
import com.system.application.shared.services.cache.keys.CacheKeys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubjectServiceImpl implements SubjectService {
    private static final Logger log =
            LoggerFactory.getLogger(SubjectServiceImpl.class);

    private final SubjectRepository subjectRepository;
    private final SchoolSubscriptionService schoolSubscriptionService;
    private final SchoolService schoolService;
    private final CacheService cacheService;

    private static final Duration SUBJECT_TTL = Duration.ofHours(20);

    public SubjectServiceImpl(
            SubjectRepository subjectRepository,
            SchoolSubscriptionService schoolSubscriptionService,
            SchoolService schoolService,
            CacheService cacheService
    ) {
        this.subjectRepository = subjectRepository;
        this.schoolSubscriptionService = schoolSubscriptionService;
        this.schoolService = schoolService;
        this.cacheService = cacheService;
    }

    @Override
    public PageResponse<SubjectResponse> findAllResponseBySchool(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando disciplinas da escola. [requisitanteId={}] [schoolId={}] [page={}] [size={}]",
                userId, school.getId(), page, size);

        String key = CacheKeys.subject(school.getId(), page, size);

        Optional<PageResponse<SubjectResponse>> cacheResponse = cacheService.get(key, new TypeReference<>(){});

        if (cacheResponse.isPresent()) {
            log.info("Disciplinas encontradas no cache. [schoolId={}] [total={}] [totalPages={}]",
                    school.getId(), cacheResponse.get().totalElements(), cacheResponse.get().totalPages());
            return cacheResponse.get();
        }

        Pageable sortedPageable =
                PageRequest.of(page, size, Sort.by("name").ascending());

        Page<SubjectResponse> responsePage =
                subjectRepository.findAllBySchoolId(school.getId(), sortedPageable)
                .map(s -> new SubjectResponse(s.getId(), s.getName()));

        log.info("Disciplinas encontradas. [schoolId={}] [total={}] [totalPages={}]",
                school.getId(), responsePage.getTotalElements(), responsePage.getTotalPages());

        PageResponse<SubjectResponse> response = PageResponse.from(responsePage);

        cacheService.set(key, response, SUBJECT_TTL);

        return response;
    }

    @Override
    public Subject findById(UUID subjectId) {
        log.info("Buscando disciplina pelo id. [subjectId={}]",
                subjectId);

        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> {
                    log.warn("Disciplina nao encontrada. [subjectId={}]", subjectId);
                    return new NotFoundObjectException("Disciplina nao encontrada");
                });
    }

    @Override
    @Transactional
    public Subject save(UUID userId, SubjectRequest request) {
        School school = schoolService.findByUserId(userId);

        log.info("Iniciando cadastro de disciplina. [requisitanteId={}] [schoolId={}] [nome={}]",
                userId, school.getId(), request.name());

        ensureSchoolHasActiveSubscription(school.getId());

        Subject subject = new Subject(null, school, request.name());
        subject = subjectRepository.save(subject);

        log.info("Disciplina cadastrada com sucesso. [subjectId={}] [schoolId={}] [nome={}]",
                subject.getId(), school.getId(), subject.getName());

        String key = CacheKeys.subjectPattern(school.getId());

        log.info("Apagando todos os cache de Disciplina ligado à escola. [school={}] [key={}]",
                school.getId(), key);

        cacheService.evictByPattern(key);

        return subject;
    }

    @Override
    @Transactional
    public void update(UUID userId, UUID subjectId, SubjectRequest request) {
        log.info("Iniciando atualizacao de disciplina. [requisitanteId={}] [subjectId={}]",
                userId, subjectId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        Subject subject = findById(subjectId);
        checkSubjectBelongsToSchool(school, subject);

        subject.setName(request.name());

        log.info("Disciplina atualizada com sucesso. [subjectId={}] [schoolId={}] [nome={}]",
                subjectId, school.getId(), request.name());

        String key = CacheKeys.subjectPattern(school.getId());

        log.info("Apagando todos os cache de Disciplina ligado à escola. [school={}] [key={}]",
                school.getId(), key);

        cacheService.evictByPattern(key);
    }

    @Override
    @Transactional
    public void deleteById(UUID userId, UUID subjectId) {
        log.info("Iniciando exclusao de disciplina. [requisitanteId={}] [subjectId={}]",
                userId, subjectId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        Subject subject = findById(subjectId);
        checkSubjectBelongsToSchool(school, subject);

        subjectRepository.deleteById(subject.getId());

        log.info("Disciplina excluida com sucesso. [subjectId={}] [schoolId={}]",
                subjectId, school.getId());

        String key = CacheKeys.subjectPattern(school.getId());

        log.info("Apagando todos os cache de Disciplina ligado à escola. [school={}] [key={}]",
                school.getId(), key);

        cacheService.evictByPattern(key);
    }

    private void checkSubjectBelongsToSchool(School school, Subject subject) {
        if (!school.getId().equals(subject.getSchool().getId())) {
            log.warn("Tentativa de acesso a disciplina de outra escola. [subjectId={}] [subjectSchoolId={}] [schoolId={}]",
                    subject.getId(), subject.getSchool().getId(), school.getId());
            throw new AccessDeniedException("Não pode alterar o disciplina de outra escola");
        }
    }

    private void ensureSchoolHasActiveSubscription(UUID schoolId) {
        try {
            schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId);
        }
        catch (SubscriptionException e) {
            log.warn("Operacao bloqueada: escola sem licenca ativa. [schoolId={}]", schoolId);
            throw new SubscriptionException("A escola não possui licença ativa.");
        }
    }
}
