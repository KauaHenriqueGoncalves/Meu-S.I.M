package com.system.application.modules.identity.user.repository;

import com.system.application.modules.identity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByCpf(String cpf);

    @Query("""
    SELECT COUNT(u) > 0 FROM User u
    WHERE u.email = :email
    OR u.cpf = :cpf
    OR u.phoneNumber = :phone
    """)
    Boolean existsConflict(@Param("email") String email, @Param("cpf") String cpf, @Param("phone") String phone);

    @Query("""
    select u
    from User u
    join u.role r
    where u.email = :email
        and (
             r.name = 'SYSTEM_ADMIN'
             or exists (
                 select 1 from SchoolAdmin sa
                 join sa.school s
                 where sa.user = u
                   and s.nameCode = :schoolCode
             )
             or exists (
                 select 1 from Collaborator c
                 join c.school s
                 where c.user = u
                   and s.nameCode = :schoolCode
             )
             or exists (
                 select 1 from LegalGuardian lg
                 join lg.school s
                 where lg.user = u
                   and s.nameCode = :schoolCode
             )
        )
    """)
    Optional<User> findForLogin(@Param("email") String email, @Param("schoolCode") String schoolCode);
}
