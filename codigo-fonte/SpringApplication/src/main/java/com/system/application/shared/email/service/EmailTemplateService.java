package com.system.application.shared.email.service;

import java.util.Map;

public interface EmailTemplateService {
    String process(String template, Map<String, Object> variables);
}
