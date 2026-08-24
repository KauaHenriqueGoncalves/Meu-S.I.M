package com.meusim.application.modules.identity.profile.schooladmin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meusim.application.auth.service.AuthenticatedUserService;
import com.meusim.application.modules.identity.base.role.Role;
import com.meusim.application.modules.identity.base.user.facade.UserFacade;
import com.meusim.application.modules.identity.profile.schooladmin.cache.SchoolAdminCacheKeys;
import com.meusim.application.modules.identity.profile.schooladmin.dto.UpdateSchoolAdminRequestDTO;
import com.meusim.application.modules.identity.profile.schooladmin.validator.SchoolAdminValidator;
import com.meusim.application.modules.school.School;
import com.meusim.application.modules.school.dto.CreateSchoolRequestDTO;
import com.meusim.application.modules.school.facade.SchoolFacade;
import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.meusim.application.modules.identity.profile.schooladmin.repository.SchoolAdminRepository;
import com.meusim.application.modules.identity.base.user.User;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.shared.exception.NotFoundObjectException;
import com.meusim.application.shared.services.cache.CacheService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class SchoolAdminServiceImpl implements SchoolAdminService {
    private static final Logger log = LoggerFactory.getLogger(SchoolAdminServiceImpl.class);
    private final SchoolAdminRepository schoolAdminRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final CacheService cacheService;
    private final SchoolAdminValidator schoolAdminValidator;
    private final UserFacade userFacade;
    private final SchoolFacade schoolFacade;

    public SchoolAdminServiceImpl(
            SchoolAdminRepository schoolAdminRepository,
            AuthenticatedUserService authenticatedUserService,
            CacheService cacheService,
            SchoolAdminValidator schoolAdminValidator,
            UserFacade userFacade,
            SchoolFacade schoolFacade) {
        this.schoolAdminRepository = schoolAdminRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.cacheService = cacheService;
        this.schoolAdminValidator = schoolAdminValidator;
        this.userFacade = userFacade;
        this.schoolFacade = schoolFacade;
    }

    @Override
    public Page<SchoolAdmin> pageBySchool(String name, int page, int size) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        School ownerSchool = schoolFacade.getEntityByOwnerIdWithCache();
        String nameFilter = (name != null && !name.isBlank()) ? name.trim() : null;
        log.info("Buscando administradores do reforco com pagina no banco. [ownerId={}] [ownerSchoolId={}] [page={}] [size={}] [nameFilter={}]",
                ownerId, ownerSchool.getId(), page, size, nameFilter);
        Pageable sortedPageable = PageRequest.of(page, size, Sort.by("user.username").ascending());
        Page<SchoolAdmin> pageResponse = schoolAdminRepository.findAllBySchoolIdAndName(ownerSchool.getId(), nameFilter, sortedPageable);
        log.info("Administradores do reforco encontrado. [ownerId={}] [ownerSchoolId={}] [number={}] [size={}] [totalPages={}] [totalElements={}]",
                ownerId, ownerSchool.getId(), pageResponse.getNumber(), pageResponse.getSize(), pageResponse.getTotalPages(), pageResponse.getTotalElements());
        return pageResponse;
    }

    @Override
    public Page<SchoolAdmin> pageBySchoolWithCache(String name, int page, int size) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        School ownerSchool = schoolFacade.getEntityByOwnerIdWithCache();
        String nameFilter = (name != null && !name.isBlank()) ? name.trim() : null;
        log.info("Buscando todos os administradores do reforco - With Cache. [ownerId={}]", ownerId);
        String key = SchoolAdminCacheKeys.page(ownerSchool.getId(), page, size, name);
        Optional<Page<SchoolAdmin>> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Admnistradores do reforco encontrados no cache. [ownerId={}] [ownerSchoolId={}] [number={}] [size={}] [totalPages={}] [totalElements={}]",
                    ownerId, ownerSchool.getId(), cache.get().getNumber(), cache.get().getSize(), cache.get().getTotalPages(), cache.get().getTotalElements());
            return cache.get();
        }
        Page<SchoolAdmin> pageAdmins = pageBySchool(nameFilter, page, size);
        log.info("Admnistradores do encontrados e inserir no cache. [ownerId={}] [ownerSchoolId={}] [number={}] [size={}]",
                ownerId, ownerSchool.getId(), pageAdmins.getNumber(), pageAdmins.getSize());
        cacheService.set(key, pageAdmins, SchoolAdminCacheKeys.TTL);
        return pageAdmins;
    }

    @Override
    public SchoolAdmin findById(UUID id) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando administrador do reforco pelo ID no banco. [ownerId={}] [id={}]",
                ownerId, id);
        SchoolAdmin admin = schoolAdminRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("O administrador do reforco não foi encontrado pelo ID no banco. [ownerId={}] [id={}]",
                            ownerId, id);
                    return new NotFoundObjectException("Administrador da escola não foi encontrado");
                });
        log.info("Adminstrador do reforco encontrado com sucesso pelo ID no banco de dados. [ownerId={}] [id={}]",
                ownerId, id);
        return admin;
    }

    @Override
    public SchoolAdmin findByIdWithCache(UUID id) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        School ownerSchool = schoolFacade.getEntityByOwnerIdWithCache();
        log.info("Buscando administrador do reforco pelo ID no banco - with cache. [ownerId={}] [ownerSchoolId={}] [id={}]",
                ownerId, ownerSchool.getId(), id);
        String key = SchoolAdminCacheKeys.byId(ownerSchool.getId(), id);
        Optional<SchoolAdmin> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Adminstrador do reforco encontrado com sucesso pelo cache. [ownerId={}] [ownerSchoolId={}] [id={}]",
                    ownerId, ownerSchool.getId(), id);
            return cache.get();
        }
        SchoolAdmin admin = findById(id);
        schoolAdminValidator.ensureSchoolAdminAndOwnerBelongsSameSchool(ownerSchool, admin.getSchool());
        log.info("Adminstrador do reforco encontrado com sucesso e inserido no cache. [ownerId={}] [ownerSchoolId={}] [id={}]",
                ownerId, ownerSchool.getId(), id);
        cacheService.set(key, admin, SchoolAdminCacheKeys.TTL);
        return admin;
    }

    @Override
    public SchoolAdmin findByUserId(UUID userId) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando administrador do reforco pelo userId no banco. [ownerId={}] [userId={}]",
                ownerId, userId);
        SchoolAdmin admin = schoolAdminRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("O administrador do reforco não foi encontrado pelo userId no banco. [ownerId={}] [userId={}]",
                            ownerId, userId);
                    return new NotFoundObjectException("Administrador da escola não foi encontrado");
                });
        log.info("Adminstrador do reforco encontrado com sucesso pelo userId. [ownerId={}] [userId={}]",
                ownerId, userId);
        return admin;
    }

    @Override
    public SchoolAdmin findByUserIdWithCache(UUID userId) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        School ownerSchool = schoolFacade.getEntityByOwnerIdWithCache();
        log.info("Buscando administrador do reforco pelo userId - with cache. [ownerId={}] [ownerSchoolId={}] [userId={}]",
                ownerId, ownerSchool.getId(), userId);
        String key = SchoolAdminCacheKeys.byUserId(ownerSchool.getId(), userId);
        Optional<SchoolAdmin> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Adminstrador do reforco encontrado pelo userId no cache. [ownerId={}] [ownerSchoolId={}] [userId={}]",
                    ownerId, ownerSchool.getId(), userId);
            return cache.get();
        }
        SchoolAdmin admin = findByUserId(userId);
        log.info("Adminstrador do reforco encontrado pelo userId e inserido no cache. [ownerId={}] [ownerSchoolId={}] [userId={}]",
                ownerId, ownerSchool.getId(), userId);
        cacheService.set(key, admin, SchoolAdminCacheKeys.TTL);
        return admin;
    }

    @Override
    @Transactional
    public SchoolAdmin create(CreateUserRequestDTO dto) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Criando um novo admnistrador do reforco. [ownerId={}] [username={}] [email={}] [cpf={}]",
                ownerId, dto.username(), dto.email(), dto.cpf());
        School ownerSchool = schoolFacade.getEntityByOwnerId();
        schoolAdminValidator.ensureSubscriptionSupportsSchoolAdminCount(ownerSchool);
        User user = userFacade.createUserRole(dto, Role.Values.SCHOOL_ADMIN);
        SchoolAdmin admin = schoolAdminRepository.save(new SchoolAdmin(null, user, ownerSchool));
        log.info("Novo adminstrador do reforco criado com sucesso e deletando caches relacionados. [ownerId={}] [newSchoolAdminId={}] [username={}] [email={}]",
                ownerId, admin, dto.username(), dto.email());
        cacheService.evictByPattern(SchoolAdminCacheKeys.pagePattern(ownerSchool.getId()));
        return admin;
    }

    @Override
    @Transactional
    public SchoolAdmin createNewSchool(CreateUserRequestDTO createUserDto, CreateSchoolRequestDTO createSchoolDto) {
        log.info("Criando um reforco escolar com os dados de cadastro do usuario. [username={}] [email={}] [cpf={}] [nameCode={}] [schoolName={}] [cnpj={}]",
                createUserDto.username(), createUserDto.email(), createUserDto.cpf(), createSchoolDto.nameCode(), createSchoolDto.schoolName(), createSchoolDto.cnpj());
        User user = userFacade.createUserRole(createUserDto, Role.Values.SCHOOL_ADMIN);
        School school = schoolFacade.create(createSchoolDto);
        SchoolAdmin schoolAdmin = schoolAdminRepository.save(new SchoolAdmin(null, user, school));
        log.info("Reforco escolar e administrador escolar criado com sucesso. [userId={}] [schoolAdminId={}] [schoolId={}]",
                schoolAdmin.getUser().getId(), schoolAdmin.getId(), schoolAdmin.getSchool().getId());
        return schoolAdmin;
    }

    @Override
    @Transactional
    public SchoolAdmin update(UUID id, UpdateSchoolAdminRequestDTO dto) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Iniciando a atulizacao do admisnitrador do reforco. [ownerId={}] [id={}]", ownerId, id);
        School ownerSchool = schoolFacade.getEntityByOwnerId();
        SchoolAdmin admin = findById(id);
        log.info("Informacaoes do admisnitrador do reforco. [ownerId={}] [id={}] [oldUsername={}] [oldEmail={}] [oldPhoneNumber={}] [oldAddress={}] [oldIsActive={}]",
                ownerId, id, admin.getUser().getUsername(), admin.getUser().getEmail(), admin.getUser().getPhoneNumber(), admin.getUser().getAddress(), admin.getUser().getActive());
        schoolAdminValidator.ensureSchoolAdminAndOwnerBelongsSameSchool(ownerSchool, admin.getSchool());
        schoolAdminValidator.ensureSchoolHasSubscription(admin.getSchool());
        admin.getUser().setUsername(dto.username());
        admin.getUser().setEmail(dto.email());
        admin.getUser().setPhoneNumber(dto.phoneNumber());
        admin.getUser().setAddress(dto.address());
        admin.getUser().setActive(dto.isActive());
        log.info("Admistrador do reforco atualizado com sucesso e deletando caches relacionados. [ownerId={}] [id={}] [newUsername={}] [newEmail={}] [newPhoneNumber={}] [newAddress={}] [newIsActive={}]",
                ownerId, id, dto.username(), dto.email(), dto.phoneNumber(), dto.address(), dto.isActive());
        cacheService.delete(SchoolAdminCacheKeys.byId(ownerSchool.getId(), admin.getId()));
        cacheService.evictByPattern(SchoolAdminCacheKeys.pagePattern(ownerSchool.getId()));
        return admin;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Deletando adminstrador do reforco. [ownerId={}] [id={}]", ownerId, id);
        School ownerSchool = schoolFacade.getEntityByOwnerId();
        SchoolAdmin admin = findById(id);
        schoolAdminValidator.ensureSchoolAdminAndOwnerBelongsSameSchool(ownerSchool, admin.getSchool());
        schoolAdminRepository.deleteById(id);
        log.info("Deletado adminstrador do reforco com sucesso e deletando os caches relacionados. [ownerId={}] [id={}]",
                ownerId, id);
        cacheService.delete(SchoolAdminCacheKeys.byId(ownerSchool.getId(), admin.getId()));
        cacheService.evictByPattern(SchoolAdminCacheKeys.pagePattern(ownerSchool.getId()));
    }
}
