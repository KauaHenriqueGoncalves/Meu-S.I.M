package com.system.application.modules.identity.systemadmin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.systemadmin.SystemAdmin;
import com.system.application.modules.identity.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.system.application.modules.identity.systemadmin.repository.SystemAdminRepository;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.modules.identity.user.service.UserService;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.services.cache.CacheService;
import com.system.application.shared.services.cache.keys.CacheKeys;
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
    private final UserService userService;
    private final CacheService cacheService;
    private final BCryptPasswordEncoder passwordEncoder;

    public SystemAdminServiceImpl(
            SystemAdminRepository systemAdminRepository,
            UserService userService,
            CacheService cacheService,
            BCryptPasswordEncoder passwordEncoder) {
        this.systemAdminRepository = systemAdminRepository;
        this.userService = userService;
        this.cacheService = cacheService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<SystemAdmin> findAll(UUID userId) {
        log.info("Buscando todos os administradores do sistema. [userId={}]", userId);
        String key = CacheKeys.systemAdminFindAll();
        Optional<List<SystemAdmin>> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Admnistradores do systema encontrados no cache. [userId={}] [size={}]", userId, cache.get().size());
            return cache.get();
        }
        List<SystemAdmin> admins = systemAdminRepository.findAll();
        log.info("Admnistradores do systema encontrados. [userId={}] [size={}]", userId, admins.size());
        cacheService.set(key, admins, SYSTEM_ADMIN_TTL);
        return admins;
    }

    @Override
    public SystemAdmin findById(UUID userId, UUID id) {
        log.info("Buscando administrador do sistema pelo ID. [userId={}] [id={}]", userId, id);
        return systemAdminRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("O administrador não foi encontrado pelo ID. [userId={}] [id={}]", userId, id);
                    return new NotFoundObjectException("Não encontrou o adminstrador");
                });
    }

    @Override
    public SystemAdmin findByIdWithCache(UUID userId, UUID id) {
        log.info("Buscando administrador do sistema pelo ID - view details. [userId={}] [id={}]", userId, id);
        String key = CacheKeys.systemAdmin(id);
        Optional<SystemAdmin> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Administrador do sistema encontrado no cache. [userId={}] [id={}]", userId, id);
            return cache.get();
        }
        SystemAdmin admin = findById(userId, id);
        log.info("Administrador do sistema encontrado. [userId={}] [id={}]", userId, id);
        cacheService.set(key, admin, SYSTEM_ADMIN_TTL);
        return admin;
    }

    @Override
    public SystemAdmin findByCpfAndEmail(String cpf, String email) {
        return systemAdminRepository.findByUserCpfAndUserEmail(cpf, email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais incorretas"));
    }

    @Override
    @Transactional
    public SystemAdmin save(UUID userId, UserRequest request) {
        log.info("Criando um administrador do sistema. [userId={}]", userId);
        User user = userService.registerUserWithRole(request, Role.Values.SYSTEM_ADMIN);
        SystemAdmin systemAdmin = new SystemAdmin(null, user);
        systemAdmin = systemAdminRepository.save(systemAdmin);
        log.info("Admistrador do sistema criado. [userId={}] [systemAdminId={}]", user, systemAdmin.getId());
        cacheService.delete(CacheKeys.systemAdminFindAll());
        return systemAdmin;
    }

    @Override
    @Transactional
    public void update(UUID userId, UUID id, UpdateSystemAdminRequestDTO update) {
        log.info("Atualizando o admisnitrador do sistema. [userId={}] [id={}]", userId, id);
        SystemAdmin admin = findById(userId, id);
        admin.getUser().setUsername(update.username());
        admin.getUser().setEmail(update.email());
        admin.getUser().setPhoneNumber(update.phoneNumber());
        admin.getUser().setAddress(update.address());
        admin.getUser().setActive(update.isActive());
        log.info("Atualizado o administrador do sistema com sucesso e deletando os caches relacionados. [userId={}] [id={}]", userId, id);
        cacheService.delete(CacheKeys.systemAdminFindAll());
        cacheService.delete(CacheKeys.systemAdmin(id));
    }

    @Override
    @Transactional
    public void updatePassword(UUID userId, UUID id, PasswordRequest request) {
        log.info("Atualizando a senha do adminstrador do sistema. [userId={}] [id={}]", userId, id);
        SystemAdmin admin = findById(userId, id);
        admin.getUser().setPassword(passwordEncoder.encode(request.newPassword()));
        log.info("Atualizando a senha do adminstrador do sistema com sucesso. [userId={}] [id={}]", userId, id);
    }

    @Override
    @Transactional
    public void deleteById(UUID userId, UUID id) {
        log.info("Deletando adminstrador do sistema. [userId={}] [id={}]", userId, id);
        SystemAdmin admin = findById(userId, id);
        systemAdminRepository.deleteById(id);
        log.info("Deletado adminstrador do sistema com sucesso e deletando os caches relacionados. [userId={}] [id={}]", userId, id);
        cacheService.delete(CacheKeys.systemAdminFindAll());
        cacheService.delete(CacheKeys.systemAdmin(id));
    }
}
