package com.system.application.domain.classType.repository;

import com.system.application.domain.classType.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {
}
