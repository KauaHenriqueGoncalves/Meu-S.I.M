package com.system.application.integration.payment.mercadopago.service;

import com.system.application.modules.licensing.schoolsubscription.dto.PaymentResult;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.integration.payment.mercadopago.client.MercadoPagoClient;
import com.system.application.integration.payment.mercadopago.dto.MercadoPagoPaymentResult;
import com.system.application.shared.exception.PaymentGatewayException;
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

    public ProcessPaymentNotificationService(
            MercadoPagoClient mercadoPagoClient,
            SchoolSubscriptionService schoolSubscriptionService
    ) {
        this.mercadoPagoClient = mercadoPagoClient;
        this.schoolSubscriptionService = schoolSubscriptionService;
    }

    private static final Set<String> ACCEPTED_TYPES =
            Set.of("payment.created", "payment.updated");

    public void processPayment(Long id, String type) {
        if (type == null || id == null) {
            log.debug("Notificacao do tipo ignorada. [tipo={}] [paymentId={}]", type, id);
            return;
        }

        if (!ACCEPTED_TYPES.contains(type)) {
            log.debug("Notificacao do tipo ignorada. [tipo={}] [paymentId={}]", type, id);
            return;
        }

        log.info("Processando notificacao de pagamento. [paymentId={}] [tipo={}]", id, type);

        MercadoPagoPaymentResult payment = mercadoPagoClient.getPaymentStatus(id);

        log.info("Status do pagamento obtido. [paymentId={}] [status={}] [detalhe={}] [referenceId={}]",
                payment.id(), payment.status(), payment.statusDetail(), payment.externalReference());

        if (!"approved".equals(payment.status())) {
            log.info("Pagamento nao aprovado, nenhuma acao realizada. [paymentId={}] [status={}] [detalhe={}]",
                    payment.id(), payment.status(), payment.statusDetail());
            return;
        }

        UUID subscriptionId;
        try {
            subscriptionId = UUID.fromString(payment.externalReference());
        }
        catch (IllegalArgumentException e) {
            log.error("Referencia externa invalida, nao é um UUID. [paymentId={}] [externalReference={}]",
                    payment.id(), payment.externalReference(), e);
            throw new PaymentGatewayException("Referencia externa invalida recebida do MercadoPago. [paymentId=" + payment.id() + "] - " + e);
        }

        log.info("Ativando assinatura. [subscriptionId={}] [paymentId={}]",
                subscriptionId, payment.id());

        String preferenceId = schoolSubscriptionService.activeById(
                subscriptionId,
                new PaymentResult(
                        payment.paymentMethod(),
                        payment.paymentType(),
                        payment.installments(),
                        payment.orderId(),
                        payment.paidAt()
                )
        );

        log.info("Assinatura ativada com sucesso. [subscriptionId={}] [paymentId={}]",
                subscriptionId, payment.id());

        mercadoPagoClient.expirePreference(preferenceId);
    }
}
