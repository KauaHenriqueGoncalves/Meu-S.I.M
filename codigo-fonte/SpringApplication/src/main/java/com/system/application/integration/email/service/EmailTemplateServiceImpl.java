package com.system.application.integration.email.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {
    private final TemplateEngine templateEngine;

    public EmailTemplateServiceImpl(
            TemplateEngine templateEngine
    ) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String process(String template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(template, context);
    }
}
