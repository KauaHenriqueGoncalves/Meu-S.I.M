package com.system.application.shared.email.service;

public interface EmailSendService {
    void sendConfirmAccountEmail(String to, String name, String link);
}
