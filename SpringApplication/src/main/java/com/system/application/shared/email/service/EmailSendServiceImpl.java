package com.system.application.shared.email.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailSendServiceImpl implements EmailSendService {
    private final JavaMailSender mailSender;
    private final EmailTemplateServiceImpl templateService;

    public EmailSendServiceImpl(JavaMailSender mailSender,
                                EmailTemplateServiceImpl templateService) {
        this.mailSender = mailSender;
        this.templateService = templateService;
    }

    @Override
    @Async("taskExecutor")
    public void sendConfirmAccountEmail(String to, String name, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            Map<String, Object> variables = Map.of(
                    "name", name,
                    "confirmationLink", link
            );
            String html = templateService.process("email/confirm-account", variables);
            helper.setTo(to);
            helper.setSubject("Confirme sua conta - Meu S.I.M");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }
    }
}
