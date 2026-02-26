package com.system.core.application.domain.classtype.repository;

import com.system.core.application.domain.classtype.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {
}
