package com.system.application.integration.payment.mercadopago.webhook;

import com.system.application.integration.payment.mercadopago.event.PaymentNotificationEvent;
import com.system.application.integration.payment.mercadopago.webhook.dto.MercadoPagoNotification;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
public class MercadoPagoWebhook {
    private static final Logger log =
            LoggerFactory.getLogger(MercadoPagoWebhook.class);

    private final MercadoPagoWebhookValidator webhookValidator;
    private final ApplicationEventPublisher eventPublisher;

    public MercadoPagoWebhook(
            MercadoPagoWebhookValidator webhookValidator,
            ApplicationEventPublisher eventPublisher
    ) {
        this.webhookValidator = webhookValidator;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/mercado-pago")
    public ResponseEntity<Void> mercadoPagoWebhook(
            @RequestBody @Valid MercadoPagoNotification notification,
            HttpServletRequest request
    ) {
        log.info("Webhook received: action={}, dataId={}", notification.action(), notification.data().id());

        if (!webhookValidator.isValid(request, notification.data().id())) {
            log.warn("Invalid webhook signature: dataId={}", notification.data().id());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        eventPublisher.publishEvent(
                new PaymentNotificationEvent(
                        Long.parseLong(notification.data().id()),
                        notification.action()
                )
        );

        return ResponseEntity.ok().build();
    }
}
