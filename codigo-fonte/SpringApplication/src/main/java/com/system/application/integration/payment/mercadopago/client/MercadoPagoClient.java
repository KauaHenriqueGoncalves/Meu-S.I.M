package com.system.application.integration.payment.mercadopago.client;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.system.application.integration.payment.gateway.dto.CheckoutRequest;
import com.system.application.integration.payment.gateway.dto.CheckoutResponse;
import com.system.application.integration.payment.mercadopago.dto.MercadoPagoPaymentResult;
import com.system.application.shared.exception.PaymentGatewayException;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class MercadoPagoClient {
    private static final Logger log =
            LoggerFactory.getLogger(MercadoPagoClient.class);

    @Value("${api.v1.mercado.pago.access-token}")
    private String accessToken;

    @Value("${api.v1.mercado.pago.notification}")
    private String notificationUrl;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
        log.info("MercadoPagoClient inicializado com sucesso.");
    }

    public CheckoutResponse createPreference(CheckoutRequest request) {
        String subscriptionId = request.referenceId().toString();

        log.info("Criando preferencia no MercadoPago. [referenceId={}] [pagador={}] [valor={}]",
                subscriptionId,
                request.payer() != null ? request.payer().email() : "nao informado",
                request.amount());

        try {
            PreferenceClient preferenceClient = new PreferenceClient();

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(subscriptionId)
                    .title(request.title())
                    .description(request.description())
                    .quantity(1)
                    .unitPrice(request.amount())
                    .build();

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .name(request.payer().name())
                    .email(request.payer().email())
                    .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://overderisive-klara-punctually.ngrok-free.dev/api/v1/auth/payment/success")
                    .pending("https://overderisive-klara-punctually.ngrok-free.dev/api/v1/auth/payment/pending")
                    .failure("https://overderisive-klara-punctually.ngrok-free.dev/api/v1/auth/payment/failure")
                    .build();

            PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                    .excludedPaymentTypes(
                            List.of(
                                    PreferencePaymentTypeRequest.builder().id("ticket").build(),
                                    PreferencePaymentTypeRequest.builder().id("atm").build(),
                                    PreferencePaymentTypeRequest.builder().id("prepaid_card").build()
                            )
                    )
                    .installments(request.installments())
                    .defaultInstallments(1)
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(List.of(item))
                    .payer(payer)
                    .backUrls(backUrls)
                    .notificationUrl(notificationUrl)
                    .paymentMethods(paymentMethods)
                    .externalReference(subscriptionId)
                    .autoReturn("approved")
                    .expires(true)
                    .expirationDateTo(OffsetDateTime.now().plusMinutes(30))
                    .build();

            Preference preference = preferenceClient.create(preferenceRequest);

            log.info("Preferencia criada com sucesso no MercadoPago. [referenceId={}] [preferenceId={}]",
                    request.referenceId(), preference.getId());

            return new CheckoutResponse(preference.getId(), preference.getInitPoint());
        }
        catch (MPApiException e) {
            log.error("Erro na API do MercadoPago ao criar preferencia. [referenceId={}] [httpStatus={}] [resposta={}]",
                    request.referenceId(), e.getStatusCode(), e.getApiResponse().getContent(), e);
            throw new PaymentGatewayException(
                    "Erro retornado pela API do MercadoPago ao criar preferencia. [referenceId=" + request.referenceId() + "]");
        }
        catch (MPException e) {
            log.error("Erro interno do SDK do MercadoPago ao criar preferencia. [referenceId={}] [motivo={}]",
                    request.referenceId(), e.getMessage(), e);
            throw new PaymentGatewayException(
                    "Falha no SDK do MercadoPago ao criar preferencia. [referenceId=" + request.referenceId() + "]");
        }
    }

    public MercadoPagoPaymentResult getPaymentStatus(long paymentId) {
        log.info("Consultando status do pagamento no MercadoPago. [paymentId={}]", paymentId);

        try {
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(paymentId);

            if (payment == null) {
                log.warn("Pagamento nao encontrado no MercadoPago. [paymentId={}]", paymentId);
                throw new PaymentGatewayException(
                        "Pagamento nao encontrado no MercadoPago. [paymentId=" + paymentId + "]");
            }

            log.info("Status do pagamento obtido com sucesso. [paymentId={}] [status={}] [detalhe={}]",
                    paymentId, payment.getStatus(), payment.getStatusDetail());

            return getPaymentResponse(payment);
        }
        catch (MPApiException e) {
            log.error("Erro na API do MercadoPago ao consultar pagamento. [paymentId={}] [httpStatus={}] [resposta={}]",
                    paymentId, e.getStatusCode(), e.getApiResponse().getContent(), e);
            throw new PaymentGatewayException(
                    "Erro retornado pela API do MercadoPago ao consultar pagamento. [paymentId=" + paymentId + "]");
        }
        catch (MPException e) {
            log.error("Erro interno do SDK do MercadoPago ao consultar pagamento. [paymentId={}] [motivo={}]",
                    paymentId, e.getMessage(), e);
            throw new PaymentGatewayException(
                    "Falha no SDK do MercadoPago ao consultar pagamento. [paymentId=" + paymentId + "]");
        }
    }

    public void expirePreference(String preferenceId) {
        log.info("Expirando preferencia no MercadoPago. [preferenceId={}]", preferenceId);

        try {
            PreferenceClient preferenceClient = new PreferenceClient();
            preferenceClient.update(
                    preferenceId,
                    PreferenceRequest.builder()
                            .expires(true)
                            .expirationDateTo(OffsetDateTime.now())
                            .build()
            );

            log.info("Preferencia expirada com sucesso. [preferenceId={}]", preferenceId);
        }
        catch (MPException | MPApiException e) {
            // Loga mas não lança, o pagamento já foi confirmado
            // A expiração é uma proteção extra, não crítica
            log.warn("Falha ao expirar preferencia no MercadoPago (nao critico). [preferenceId={}] [motivo={}]",
                    preferenceId, e.getMessage());
        }
    }

    private MercadoPagoPaymentResult getPaymentResponse(Payment paymentMercadoPago) {
        return new MercadoPagoPaymentResult(
                paymentMercadoPago.getId().toString(),
                paymentMercadoPago.getOrder().getId().toString(),
                paymentMercadoPago.getExternalReference(),
                paymentMercadoPago.getStatus(),
                paymentMercadoPago.getStatusDetail(),
                paymentMercadoPago.getInstallments(),
                paymentMercadoPago.getPaymentMethodId(),
                paymentMercadoPago.getPaymentTypeId(),
                paymentMercadoPago.getDateApproved()
        );
    }
}
