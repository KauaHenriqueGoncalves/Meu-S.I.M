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

    @EventListener
    @Async("paymentExecutor")
    public void handle(PaymentNotificationEvent event) {
        log.info("Evento de notificacao de pagamento recebido. [resourceId={}] [resourceType={}]",
                event.resourceId(), event.resourceType());

        try {
            service.processPayment(event.resourceId(), event.resourceType());

            log.info("Evento de notificacao de pagamento processado com sucesso. [resourceId={}] [resourceType={}]",
                    event.resourceId(), event.resourceType());
        }
        catch (Exception e) {
            log.error("Falha ao processar evento de notificacao de pagamento. [resourceId={}] [resourceType={}] [motivo={}]",
                    event.resourceId(), event.resourceType(), e.getMessage(), e);
        }
    }
}
