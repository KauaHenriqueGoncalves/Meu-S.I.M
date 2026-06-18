package com.system.application.integration.email.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailSendServiceImpl implements EmailSendService {
    private static final Logger log =
            LoggerFactory.getLogger(EmailSendServiceImpl.class);

    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;

    public EmailSendServiceImpl(
            JavaMailSender mailSender,
            EmailTemplateService templateService
    ) {
        this.mailSender = mailSender;
        this.templateService = templateService;
    }

    @Override
    @Async("emailExecutor")
    public void sendConfirmAccountEmail(String to, String name, String schoolCode, String link) {
        log.info("Enviando e-mail de confirmacao de conta. [destinatario={}] [nome={}]", to, name);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            Map<String, Object> variables = Map.of(
                    "name", name,
                    "email", to,
                    "schoolCode", schoolCode,
                    "confirmationLink", link
            );
            String html = templateService.process("email/confirm-account", variables);
            helper.setTo(to);
            helper.setSubject("Confirme sua conta - Meu S.I.M");
            helper.setText(html, true);
            mailSender.send(message);

            log.info("E-mail de confirmacao de conta enviado com sucesso. [destinatario={}]", to);
        }
        catch (Exception e) {
            log.error("Falha ao enviar e-mail de confirmacao de conta. [destinatario={}] [motivo={}]",
                    to, e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar e-mail", e);
        }
    }
}
