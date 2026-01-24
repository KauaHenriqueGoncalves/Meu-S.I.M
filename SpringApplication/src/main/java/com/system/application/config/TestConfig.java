package com.system.application.config;

import com.system.application.domain.legalGuardian.LegalGuardian;
import com.system.application.domain.legalGuardian.repository.LegalGuardianRepository;
import com.system.application.domain.legalGuardian.service.LegalGuardianService;
import com.system.application.domain.role.Role;
import com.system.application.domain.role.service.RoleService;
import com.system.application.domain.school.School;
import com.system.application.domain.school.service.SchoolService;
import com.system.application.domain.student.Student;
import com.system.application.domain.student.repository.StudentRepository;
import com.system.application.domain.systemAdmin.service.SystemAdminService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {
    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private LegalGuardianRepository legalGuardianRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public void run(String... args) throws Exception {
        School school = schoolService.save(new School(null, "school_code_test", "School_Test", "11111111111111"));
        User user = userRepository.save(
                new User(
                        null,
                        "Rogerio",
                        "emaui@email.com",
                        "12345678",
                        "12312312311",
                        "(81) 12345",
                        "limoreio",
                        true,
                        Instant.now(),
                        Set.of(roleService.findByName(Role.Values.LEGAL_GUARDIAN.name()))
                )
        );

        LegalGuardian legalGuardian = legalGuardianRepository.save(
                new LegalGuardian(
                        null,
                        user,
                        school,
                        "Papai"
                )
        );
        studentRepository.save(new Student(null, school, "Rogerinho estudante", LocalDate.parse("2003-02-03"), "5 ANO", legalGuardian));

        System.out.println("Console-H2: http://localhost:8080/console-h2");
    }
}
