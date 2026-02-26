package com.system.core.application.shared.email.service;

public interface EmailSendService {
    void sendConfirmAccountEmail(String to, String name, String link);
}
