package com.system.application.modules.identify.role.service;

import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.role.repository.RoleRepository;
import com.system.application.modules.identity.role.service.RoleServiceImpl;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.services.cache.CacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleServiceImpl")
public class RoleServiceImplTest {
    @Mock private RoleRepository roleRepository;
    @Mock private CacheService cacheService;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Nested
    @DisplayName("findByName()")
    final class FindByName {
        @Test
        @DisplayName("deve retornar a role quando o nome existir")
        void shouldReturnRole_whenNameExists() {
            Role role = new Role();
            role.setId(2L);
            role.setName("school_admin");

            when(roleRepository.findByName("school_admin")).thenReturn(Optional.of(role));

            Role result = roleService.findByName("school_admin");

            assertThat(result).isEqualTo(role);
            verify(roleRepository).findByName("school_admin");
        }

        @Test
        @DisplayName("deve converter o nome para lowercase antes de buscar")
        void shouldConvertNameToLowercase_beforeQuerying() {
            Role role = new Role();
            role.setId(2L);
            role.setName("school_admin");

            when(roleRepository.findByName("school_admin")).thenReturn(Optional.of(role));

            roleService.findByName("SCHOOL_ADMIN");

            verify(roleRepository).findByName("school_admin");
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando o nome não existir")
        void shouldThrowNotFound_whenNameDoesNotExist() {
            when(roleRepository.findByName("inexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> roleService.findByName("inexistente"))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Not found Role");
        }
    }
}
