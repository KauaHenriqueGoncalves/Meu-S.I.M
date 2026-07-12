package com.system.application.integration.email.service;

import com.system.application.integration.email.dto.SendEmailSubscriptionPaid;

import java.util.List;
import java.util.UUID;

public interface EmailSendService {
    void sendConfirmAccountEmail(String to, String name, String schoolCode, String link);
    void sendSubscriptionPaidEmails(List<String> emails, UUID schoolId, SendEmailSubscriptionPaid info);
}
