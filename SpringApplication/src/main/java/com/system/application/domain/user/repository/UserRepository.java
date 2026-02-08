package com.system.application.domain.user.repository;

import com.system.application.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query(
            """
            SELECT COUNT(u) > 0 FROM User u
            WHERE u.email = :email
            OR u.cpf = :cpf
            OR u.phoneNumber = :phone
            """)
    Boolean existsConflict(@Param("email") String email, @Param("cpf") String cpf, @Param("phone") String phone);

    @Query("""
            select distinct u
            from User u
            join u.role r
            left join SchoolAdmin sa on sa.userId = u
            left join sa.schoolId s
            left join Collaborator c on c.user = u
            left join c.school s2
            left join LegalGuardian lg on lg.user = u 
            left join lg.school s3
            where u.email = :email
              and (
                   r.name = 'SYSTEM_ADMIN'
                   or s.nameCode = :schoolCode
                   or s2.nameCode = :schoolCode
                   or s3.nameCode = :schoolCode
              )
            """)
    Optional<User> findForLogin(@Param("email") String email, @Param("schoolCode") String schoolCode);
}
