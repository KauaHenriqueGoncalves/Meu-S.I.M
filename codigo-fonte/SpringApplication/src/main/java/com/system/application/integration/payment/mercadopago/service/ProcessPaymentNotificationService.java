package com.system.application.integration.payment.mercadopago.service;

import com.system.application.core.schoolsubscription.dto.PaymentResult;
import com.system.application.core.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.integration.payment.mercadopago.client.MercadoPagoClient;
import com.system.application.integration.payment.mercadopago.dto.MercadoPagoPaymentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class ProcessPaymentNotificationService {
    private static final Logger log =
            LoggerFactory.getLogger(ProcessPaymentNotificationService.class);

    private final MercadoPagoClient mercadoPagoClient;
    private final SchoolSubscriptionService schoolSubscriptionService;

    private static final Set<String> ACCEPTED_TYPES = Set.of(
            "payment.created",
            "payment.updated"
    );

    public ProcessPaymentNotificationService(
            MercadoPagoClient mercadoPagoClient,
            SchoolSubscriptionService schoolSubscriptionService
    ) {
        this.mercadoPagoClient = mercadoPagoClient;
        this.schoolSubscriptionService = schoolSubscriptionService;
    }

    public void processPayment(Long id, String type) {
        if (!ACCEPTED_TYPES.contains(type)) {
            log.debug("Notification type '{}' ignored", type);
            return;
        }

        MercadoPagoPaymentResult payment = mercadoPagoClient.getPaymentStatus(id);

        log.info("Payment received: id={}, status={}, externalReference={}",
                payment.id(), payment.status(), payment.externalReference());

        if (!"approved".equals(payment.status())) {
            log.info("Payment {} not approved, status={}. Ignoring.", payment.id(), payment.status());
            return;
        }

        UUID subscriptionId = UUID.fromString(payment.externalReference());

        schoolSubscriptionService.activeById(
                subscriptionId,
                new PaymentResult(
                        payment.paymentMethod(),
                        payment.paymentType(),
                        payment.installments(),
                        payment.orderId(),
                        payment.paidAt()
                )
        );

        log.info("Subscription {} activated successfully for payment {}", subscriptionId, payment.id());
    }
}
