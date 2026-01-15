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
}
