package com.system.application.modules.identity.systemadmin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.auth.service.AuthenticatedUserService;
import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.systemadmin.SystemAdmin;
import com.system.application.modules.identity.systemadmin.keys.SystemAdminCacheKeys;
import com.system.application.modules.identity.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.system.application.modules.identity.systemadmin.repository.SystemAdminRepository;
import com.system.application.modules.identity.systemadmin.validator.SystemAdminValidator;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.modules.identity.user.service.UserService;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.services.cache.CacheService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SystemAdminServiceImpl implements SystemAdminService {
    private static final Logger log = LoggerFactory.getLogger(SystemAdminServiceImpl.class);
    private static final Duration SYSTEM_ADMIN_TTL = Duration.ofHours(30);
    private final SystemAdminRepository systemAdminRepository;
    private final SystemAdminValidator systemAdminValidator;
    private final AuthenticatedUserService authenticatedUserService;
    private final UserService userService;
    private final CacheService cacheService;
    private final BCryptPasswordEncoder passwordEncoder;

    public SystemAdminServiceImpl(
            SystemAdminRepository systemAdminRepository,
            SystemAdminValidator systemAdminValidator,
            AuthenticatedUserService authenticatedUserService,
            UserService userService,
            CacheService cacheService,
            BCryptPasswordEncoder passwordEncoder) {
        this.systemAdminRepository = systemAdminRepository;
        this.systemAdminValidator = systemAdminValidator;
        this.authenticatedUserService = authenticatedUserService;
        this.userService = userService;
        this.cacheService = cacheService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<SystemAdmin> findAll() {
        log.info("Buscando todos os administradores do sistema.");
        List<SystemAdmin> admins =  systemAdminRepository.findAll();
        log.info("Admnistradores do sistema encontrados. [size={}]", admins.size());
        return admins;
    }

    @Override
    public List<SystemAdmin> findAllWithCache() {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando todos os administradores do sistema - With Cache. [ownerId={}]", ownerId);
        String key = SystemAdminCacheKeys.all();
        Optional<List<SystemAdmin>> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Admnistradores do sistema encontrados no cache. [ownerId={}] [size={}]", ownerId, cache.get().size());
            return cache.get();
        }
        List<SystemAdmin> admins = systemAdminRepository.findAll();
        log.info("Admnistradores do sistema encontrados e inserir no cache. [ownerId={}] [size={}]", ownerId, admins.size());
        cacheService.set(key, admins, SYSTEM_ADMIN_TTL);
        return admins;
    }

    @Override
    public SystemAdmin findById(UUID id) {
        log.info("Buscando administrador do sistema pelo ID. [id={}]", id);
        return systemAdminRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("O administrador não foi encontrado pelo ID. [id={}]", id);
                    return new NotFoundObjectException("Não encontrou o adminstrador");
                });
    }

    @Override
    public SystemAdmin findByIdWithCache(UUID id) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando administrador do sistema pelo ID - With Cache. [ownerId={}] [id={}]", ownerId, id);
        String key = SystemAdminCacheKeys.byId(id);
        Optional<SystemAdmin> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Administrador do sistema encontrado no cache. [ownerId={}] [id={}]", ownerId, id);
            return cache.get();
        }
        SystemAdmin admin = findById(id);
        log.info("Administrador do sistema encontrado. [ownerId={}] [id={}]", ownerId, id);
        cacheService.set(key, admin, SYSTEM_ADMIN_TTL);
        return admin;
    }

    @Override
    public SystemAdmin findByCpfAndEmail(String cpf, String email) {
        log.info("Buscando administrador do sistema. [cpf={}] [email={}]", cpf, email);
        SystemAdmin admin = systemAdminRepository.findByUserCpfAndUserEmail(cpf, email)
                .orElseThrow(() -> {
                    log.warn("Credenciais incorretas. [cpf={}] [email={}]", cpf, email);
                    return new BadCredentialsException("Credenciais incorretas");
                });
        log.info("Administrador encontrado. [id={}] [cpf={}] [email={}]", admin.getId().toString(), cpf, email);
        return admin;
    }

    @Override
    @Transactional
    public SystemAdmin save(UserRequest request) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Criando um administrador do sistema. [ownerId={}]", ownerId);
        User user = userService.registerUserWithRole(request, Role.Values.SYSTEM_ADMIN);
        SystemAdmin systemAdmin = new SystemAdmin(null, user);
        systemAdmin = systemAdminRepository.save(systemAdmin);
        log.info("Admistrador do sistema criado. [ownerId={}] [systemAdminId={}]", user, systemAdmin.getId());
        cacheService.delete(SystemAdminCacheKeys.all());
        return systemAdmin;
    }

    @Override
    @Transactional
    public void update(UUID id, UpdateSystemAdminRequestDTO update) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Atualizando o admisnitrador do sistema. [ownerId={}] [id={}]", ownerId, id);
        SystemAdmin admin = findById(id);
        admin.getUser().setUsername(update.username());
        admin.getUser().setEmail(update.email());
        admin.getUser().setPhoneNumber(update.phoneNumber());
        admin.getUser().setAddress(update.address());
        admin.getUser().setActive(update.isActive());
        log.info("Atualizado o administrador do sistema com sucesso e deletando os caches relacionados. [ownerId={}] [id={}]", ownerId, id);
        cacheService.delete(SystemAdminCacheKeys.all());
        cacheService.delete(SystemAdminCacheKeys.byId(id));
    }

    @Override
    @Transactional
    public void updatePassword(UUID id, PasswordRequest request) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Atualizando a senha do adminstrador do sistema. [ownerId={}] [id={}]", ownerId, id);
        SystemAdmin admin = findById(id);
        admin.getUser().setPassword(passwordEncoder.encode(request.newPassword()));
        log.info("Atualizando a senha do adminstrador do sistema com sucesso. [ownerId={}] [id={}]", ownerId, id);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Deletando adminstrador do sistema. [ownerId={}] [id={}]", ownerId, id);
        SystemAdmin admin = findById(id);
        systemAdminRepository.deleteById(id);
        log.info("Deletado adminstrador do sistema com sucesso e deletando os caches relacionados. [ownerId={}] [id={}]", ownerId, id);
        cacheService.delete(SystemAdminCacheKeys.all());
        cacheService.delete(SystemAdminCacheKeys.byId(id));
    }
}
