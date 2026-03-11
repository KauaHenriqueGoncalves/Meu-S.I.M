package com.system.application.integration.email.service;

import java.util.Map;

public interface EmailTemplateService {
    String process(String template, Map<String, Object> variables);
}
