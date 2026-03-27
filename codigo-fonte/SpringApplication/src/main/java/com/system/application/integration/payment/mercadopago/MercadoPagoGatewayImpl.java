package com.system.application.integration.payment.mercadopago;

import com.system.application.integration.payment.gateway.PaymentGateway;
import com.system.application.integration.payment.gateway.dto.CheckoutRequest;
import com.system.application.integration.payment.gateway.dto.CheckoutResponse;
import com.system.application.integration.payment.mercadopago.client.MercadoPagoClient;
import com.system.application.shared.exception.PaymentGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("mercadopago")
public class MercadoPagoGatewayImpl implements PaymentGateway {
    private static final Logger log =
            LoggerFactory.getLogger(MercadoPagoGatewayImpl.class);

    private final MercadoPagoClient mercadoPagoClient;

    public MercadoPagoGatewayImpl(
            MercadoPagoClient mercadoPagoClient
    ) {
        this.mercadoPagoClient = mercadoPagoClient;
    }

    @Override
    public CheckoutResponse createCheckout(CheckoutRequest request) {
        log.info("Iniciando criacao de preferencia no MercadoPago. [referenceId={}] [pagador={}] [valor={}]",
                request.referenceId(),
                request.payer() != null ? request.payer().email() : "nao informado",
                request.amount());

        try {
            CheckoutResponse response = mercadoPagoClient.createPreference(request);

            log.info("Preferencia criada com sucesso no MercadoPago. [referenceId={}] [preferenceId={}]",
                    request.referenceId(), response.preferenceId());

            return response;
        }
        catch (Exception e) {
            log.error("Falha ao criar preferencia no MercadoPago. [referenceId={}] [motivo={}]",
                    request.referenceId(), e.getMessage(), e);
            throw new PaymentGatewayException("Não foi possível criar o checkout no MercadoPago para a referência: " + request.referenceId() + ", Error: " + e);
        }
    }
}
