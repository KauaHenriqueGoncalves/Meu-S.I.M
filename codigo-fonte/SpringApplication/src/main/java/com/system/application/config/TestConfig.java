package com.system.application.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Console-H2: http://localhost:8080/api/v1/console-h2");
        System.out.println("MailHog: http://localhost:8025");
        System.out.println("Actuator: http://localhost:8080/api/v1/actuator");
    }
}
