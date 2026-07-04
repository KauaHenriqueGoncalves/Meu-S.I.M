package com.system.application.modules.academic.student.service;

import com.system.application.modules.academic.student.dto.StudentResponse;
import com.system.application.modules.academic.student.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentQueryImpl implements StudentQuery {
    private final static Logger log = LoggerFactory.getLogger(StudentQueryImpl.class);

    private final StudentRepository studentRepository;

    public StudentQueryImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<StudentResponse> findAllStudentByLegalGuardianId(UUID id) {
        if (id == null) {
            log.error("Id do Responsável é nulo. [legalGuardianId={}]", id);
            throw new IllegalArgumentException("Id do responsável é nulo");
        }

        List<StudentResponse> response = studentRepository.findAllByLegalGuardianId(id)
                .stream()
                .map(s -> new StudentResponse(s.getId(), s.getName(), s.getDateOfBirth(), s.getGrade()))
                .toList();

        log.info("Total de estudantes encontrado pelo Id do responsável. [size={}] [legalGuardianId={}]",
                response.size(), id);

        return response;
    }
}
