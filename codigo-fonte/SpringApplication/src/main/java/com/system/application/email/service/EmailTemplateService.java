package com.system.application.email.service;

import java.util.Map;

public interface EmailTemplateService {
    String process(String template, Map<String, Object> variables);
}
