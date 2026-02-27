package com.system.application.email.service;

public interface EmailSendService {
    void sendConfirmAccountEmail(String to, String name, String link);
}
