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
        log.info("MercadoPagoClient init");
    }

    public CheckoutResponse createPreference(CheckoutRequest request) {
        try {
            String subscriptionId = request.referenceId().toString();

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
                    .success("https://overderisive-klara-punctually.ngrok-free.dev/auth/payment/success")
                    .pending("https://overderisive-klara-punctually.ngrok-free.dev/auth/payment/pending")
                    .failure("https://overderisive-klara-punctually.ngrok-free.dev/auth/payment/failure")
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

            return new CheckoutResponse(preference.getId(), preference.getInitPoint());

        }
        catch (MPApiException e) {
            System.out.println("Status: " + e.getStatusCode());
            System.out.println("Response: " + e.getApiResponse().getContent());
        }
        catch (MPException e) {
            System.out.println("Erro de criar: "  + e.getMessage());
        }
        catch (RuntimeException e) {
            System.out.println("Erro: "  + e.getMessage());
        }
        return null;
    }

    public MercadoPagoPaymentResult getPaymentStatus(long id) {
        PaymentClient paymentClient = new PaymentClient();

        try {
            Payment paymentMercadoPago = paymentClient.get(id);

            if (paymentMercadoPago == null) {
                throw new MPException("Payment not found");
            }

            return getPaymentResponse(paymentMercadoPago);
        }
        catch (MPException | MPApiException ex) {
            throw new RuntimeException(ex);
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
