package com.system.application.modules.identity.profile.schooladmin.service;

import com.system.application.auth.service.AuthenticatedUserService;
import com.system.application.modules.identity.base.role.Role;
import com.system.application.modules.identity.base.user.facade.UserFacade;
import com.system.application.modules.identity.profile.schooladmin.dto.UpdateSchoolAdminRequestDTO;
import com.system.application.modules.identity.profile.schooladmin.validator.SchoolAdminValidator;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.modules.school.School;
import com.system.application.modules.school.dto.CreateSchoolRequestDTO;
import com.system.application.modules.school.facade.SchoolFacade;
import com.system.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.system.application.modules.identity.profile.schooladmin.repository.SchoolAdminRepository;
import com.system.application.modules.identity.base.user.User;
import com.system.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class SchoolAdminServiceImpl implements SchoolAdminService {
    private static final Logger log = LoggerFactory.getLogger(SchoolAdminServiceImpl.class);
    private final SchoolAdminRepository schoolAdminRepository;
    private final SchoolSubscriptionService subscriptionService;
    private final AuthenticatedUserService authenticatedUserService;
    private final SchoolAdminValidator schoolAdminValidator;
    private final UserFacade userFacade;
    private final SchoolFacade schoolFacade;

    public SchoolAdminServiceImpl(
            SchoolAdminRepository schoolAdminRepository,
            SchoolSubscriptionService subscriptionService,
            AuthenticatedUserService authenticatedUserService,
            SchoolAdminValidator schoolAdminValidator,
            UserFacade userFacade,
            SchoolFacade schoolFacade) {
        this.schoolAdminRepository = schoolAdminRepository;
        this.subscriptionService = subscriptionService;
        this.authenticatedUserService = authenticatedUserService;
        this.schoolAdminValidator = schoolAdminValidator;
        this.userFacade = userFacade;
        this.schoolFacade = schoolFacade;
    }

    @Override
    public Page<SchoolAdmin> pageBySchoolId(String name, int page, int size) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        School ownerSchool = schoolFacade.getEntityByOwnerId();
        String nameFilter = (name != null && !name.isBlank()) ? name.trim() : null;
        log.info("Buscando administradores do reforco com pagina no banco. [ownerId={}] [ownerSchoolId={}] [page={}] [size={}] [name={}]",
                ownerId, ownerSchool.getId(), page, size, nameFilter);
        Pageable sortedPageable = PageRequest.of(page, size, Sort.by("user.username").ascending());
        Page<SchoolAdmin> pageResponse = schoolAdminRepository.findAllBySchoolIdAndName(ownerSchool.getId(), nameFilter, sortedPageable);
        log.info("Administradores do reforco encontrado. [ownerId={}] [ownerSchoolId={}] [number={}] [size={}] [totalPages={}] [totalElements={}]",
                ownerId, ownerSchool.getId(), pageResponse.getNumber(), pageResponse.getSize(), pageResponse.getTotalPages(), pageResponse.getTotalElements());
        return pageResponse;
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
        log.info("Adminstrador do reforco encontrado com sucesso pelo ID. [ownerId={}] [id={}]",
                ownerId, id);
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
    @Transactional
    public SchoolAdmin create(CreateUserRequestDTO dto) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Criando um novo admnistrador do reforco. [ownerId={}] [username={}] [email={}] [cpf={}]",
                ownerId, dto.username(), dto.email(), dto.cpf());
        School ownerSchool = schoolFacade.getEntityByOwnerId();
        SchoolSubscription sub = subscriptionService.findActiveSubscriptionBySchoolId(ownerSchool.getId());
        schoolAdminValidator.ensureSubscriptionSupportsSchoolAdminCound(ownerSchool, sub);
        schoolAdminValidator.ensureSchoolHasSubscription(ownerSchool);
        User user = userFacade.createUserRole(dto, Role.Values.SCHOOL_ADMIN);
        SchoolAdmin admin = schoolAdminRepository.save(new SchoolAdmin(null, user, ownerSchool));
        log.info("Novo adminstrador do reforco criado com sucesso. [ownerId={}] [newSchoolAdminId={}] [username={}] [email={}]",
                ownerId, admin, dto.username(), dto.email());
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
        log.info("Admistrador do reforco atualizado com sucesso. [ownerId={}] [id={}] [newUsername={}] [newEmail={}] [newPhoneNumber={}] [newAddress={}] [newIsActive={}]",
                ownerId, id, dto.username(), dto.email(), dto.phoneNumber(), dto.address(), dto.isActive());
        return admin;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Deletando adminstrador do reforco. [ownerId={}] [id={}]", ownerId, id);
        SchoolAdmin admin = findById(id);
        schoolAdminRepository.deleteById(id);
        log.info("Deletado adminstrador do reforco com sucesso e deletando os caches relacionados. [ownerId={}] [id={}]", ownerId, id);
    }
}
