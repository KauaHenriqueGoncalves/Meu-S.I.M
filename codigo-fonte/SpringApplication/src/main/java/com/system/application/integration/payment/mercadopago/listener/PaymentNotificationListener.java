package com.system.application.integration.payment.mercadopago.listener;

import com.system.application.integration.payment.mercadopago.event.PaymentNotificationEvent;
import com.system.application.integration.payment.mercadopago.service.ProcessPaymentNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PaymentNotificationListener {
    private static final Logger log =
            LoggerFactory.getLogger(PaymentNotificationListener.class);

    private final ProcessPaymentNotificationService service;

    public PaymentNotificationListener(
            ProcessPaymentNotificationService service
    ) {
        this.service = service;
    }

    @Async
    @EventListener
    public void handle(PaymentNotificationEvent event) {
        try {
            service.processPayment(event.resourceId(), event.resourceType());
            log.info("PaymentNotificationEvent processed successfully: dataId={}", event.resourceId());
        } catch (Exception ex) {
            log.error("Failed to process PaymentNotificationEvent: dataId={}, error={}",
                    event.resourceId(), ex.getMessage(), ex);
        }
    }
}
