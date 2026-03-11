package com.system.application.integration.payment.mercadopago.listener;

import com.system.application.integration.payment.mercadopago.event.PaymentNotificationEvent;
import com.system.application.integration.payment.mercadopago.service.ProcessPaymentNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
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

    @EventListener
    public void handle(PaymentNotificationEvent event) {
        service.processPayment(event.resourceId(), event.resourceType());

        // TODO: apartir daqui não roda

        log.info("PaymentNotificationEvent published: dataId={}, action={}",
                event.resourceId(),  event.resourceType());
    }
}
