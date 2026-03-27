package com.system.application.integration.email.service;

public interface EmailSendService {
    void sendConfirmAccountEmail(String to, String name, String link);
}
