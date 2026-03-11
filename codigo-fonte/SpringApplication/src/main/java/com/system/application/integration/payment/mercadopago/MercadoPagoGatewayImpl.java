package com.system.application.integration.payment.mercadopago;

import com.system.application.integration.payment.gateway.PaymentGateway;
import com.system.application.integration.payment.gateway.dto.CheckoutRequest;
import com.system.application.integration.payment.gateway.dto.CheckoutResponse;
import com.system.application.integration.payment.mercadopago.client.MercadoPagoClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("mercadopago")
public class MercadoPagoGatewayImpl implements PaymentGateway {
    private final MercadoPagoClient mercadoPagoClient;

    public MercadoPagoGatewayImpl(
            MercadoPagoClient mercadoPagoClient
    ) {
        this.mercadoPagoClient = mercadoPagoClient;
    }

    @Override
    public CheckoutResponse createCheckout(CheckoutRequest request) {
        try {
            return mercadoPagoClient.createPreference(request);
        }
        catch (Exception e) {
            System.out.println("CRIANDO PREFERENCIA ERROR");
        }
        return null;
    }
}
