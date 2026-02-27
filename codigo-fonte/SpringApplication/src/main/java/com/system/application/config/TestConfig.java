package com.system.application.config;

import com.system.application.core.legalguardian.LegalGuardian;
import com.system.application.core.legalguardian.repository.LegalGuardianRepository;
import com.system.application.core.role.Role;
import com.system.application.core.role.service.RoleService;
import com.system.application.core.school.School;
import com.system.application.core.school.dto.SchoolRequest;
import com.system.application.core.school.service.SchoolService;
import com.system.application.core.student.Student;
import com.system.application.core.student.repository.StudentRepository;
import com.system.application.core.systemadmin.service.SystemAdminService;
import com.system.application.core.user.User;
import com.system.application.core.user.repository.UserRepository;
import com.system.application.email.service.EmailSendServiceImpl;
import com.system.application.auth.emailverification.service.EmailVerificationService;
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

    @Autowired
    private EmailSendServiceImpl emailServiceImpl;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Override
    public void run(String... args) throws Exception {
        School school = schoolService.save(new SchoolRequest("school_code_test", "School_Test", "11111111111111"));
        User user = userRepository.save(
                new User(
                        null,
                        "Rogerio Brito da Silva",
                        "emaui@email.com",
                        "12345678",
                        "12312312311",
                        "(81) 12345",
                        "limoreio",
                        false,
                        Instant.now(),
                        Set.of(roleService.findByName(Role.Values.LEGAL_GUARDIAN.name()))
                )
        );

        String token = emailVerificationService.createOrRefreshToken(user.getId());
        String link = "http://localhost:8080/auth/verify?token=" + token;
        emailServiceImpl.sendConfirmAccountEmail(user.getEmail(), user.getUsername(), link);

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
        System.out.println("MailHog: http://localhost:8025");
    }
}
