package com.system.application.domain.school.repository;

import com.system.application.domain.school.School;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolRepository extends CrudRepository<School, UUID> {
    Optional<School> findByNameCode(String nameCode);
    Optional<School> findBySchoolName(String schoolName);
    Optional<School> findByCnpj(String cnpj);
    @Query(
            """
            SELECT COUNT(s) > 0 FROM School s
            WHERE s.nameCode = :code
               OR s.cnpj = :cnpj
            """)
    Boolean existsConflict(@Param("code") String nameCode, @Param("cnpj") String cnpj);
}
