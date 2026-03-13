package com.system.application.modules.academic.classtype.repository;

import com.system.application.modules.academic.classtype.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {
}
