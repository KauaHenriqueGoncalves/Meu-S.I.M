package com.system.application.domain.school.repository;

import com.system.application.domain.school.School;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolRepository extends CrudRepository<School, UUID> {
    @Query("""
    SELECT COUNT(s) > 0 FROM School s
    WHERE s.nameCode = :code
        OR s.cnpj = :cnpj
    """)
    Boolean existsConflict(@Param("code") String nameCode, @Param("cnpj") String cnpj);

    @Query("""
    SELECT s FROM School s
    WHERE s.createdAt < :limit
        AND NOT EXISTS (SELECT sa FROM SchoolAdmin sa WHERE sa.school = s)
        AND NOT EXISTS (SELECT lg FROM LegalGuardian lg WHERE lg.school = s)
        AND NOT EXISTS (SELECT c FROM Collaborator c WHERE c.school = s)
        AND NOT EXISTS (SELECT st FROM Student st WHERE st.school = s)
    """)
    List<School> findAbandonedSchools(Instant limit);

    @Query("""
    SELECT s FROM School s  
        WHERE 
        EXISTS (SELECT sa FROM SchoolAdmin sa WHERE sa.school = s AND sa.user.id = :userId)
        OR EXISTS (SELECT c FROM Collaborator c WHERE c.school = s AND c.user.id = :userId)
        OR EXISTS (SELECT lg FROM LegalGuardian lg WHERE lg.school = s AND lg.user.id = :userId)
    """)
    Optional<School> findSchoolByUserId(@Param("userId") UUID userId);
}
