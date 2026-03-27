package com.system.application.integration.payment.mercadopago.client;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentOrder;
import com.mercadopago.resources.preference.Preference;
import com.system.application.integration.payment.gateway.dto.CheckoutRequest;
import com.system.application.integration.payment.gateway.dto.CheckoutResponse;
import com.system.application.integration.payment.gateway.dto.PayerInfo;
import com.system.application.integration.payment.mercadopago.dto.MercadoPagoPaymentResult;
import com.system.application.shared.exception.PaymentGatewayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MercadoPagoClient")
public class MercadoPagoClientTest {
    @InjectMocks
    private MercadoPagoClient mercadoPagoClient;

    private CheckoutRequest checkoutRequest;
    private UUID referenceId;

    @BeforeEach
    void setUp() {
        referenceId = UUID.randomUUID();

        checkoutRequest = new CheckoutRequest(
                referenceId,
                "Licenca - Plano Básico",
                "Pagamento da licenca Plano Básico",
                BigDecimal.valueOf(1200.00),
                12,
                new PayerInfo("Admin Escola", "admin@escola.com")
        );
    }

    @Nested
    @DisplayName("createPreference()")
    final class CreatePreference {
        @Test
        @DisplayName("deve retornar CheckoutResponse com preferenceId e initPoint quando sucesso")
        void shouldReturnCheckoutResponse_whenSuccess() throws MPException, MPApiException {
            Preference preference = mock(Preference.class);
            when(preference.getId()).thenReturn("pref_abc123");
            when(preference.getInitPoint()).thenReturn("https://init.mercadopago.com/abc123");

            try (MockedConstruction<PreferenceClient> mockedClient =
                         mockConstruction(PreferenceClient.class, (mock, ctx) ->
                                 when(mock.create(any())).thenReturn(preference))) {

                CheckoutResponse result = mercadoPagoClient.createPreference(checkoutRequest);

                assertThat(result.preferenceId()).isEqualTo("pref_abc123");
                assertThat(result.initPoint()).isEqualTo("https://init.mercadopago.com/abc123");
            }
        }

        @Test
        @DisplayName("deve lançar PaymentGatewayException quando MPApiException for lançada")
        void shouldThrowPaymentGatewayException_whenMPApiException() throws MPException, MPApiException {
            MPApiException apiException = mock(MPApiException.class);
            when(apiException.getStatusCode()).thenReturn(400);
            when(apiException.getApiResponse()).thenReturn(mock(com.mercadopago.net.MPResponse.class));

            try (MockedConstruction<PreferenceClient> mockedClient =
                         mockConstruction(PreferenceClient.class, (mock, ctx) ->
                                 when(mock.create(any())).thenThrow(apiException))) {

                assertThatThrownBy(() -> mercadoPagoClient.createPreference(checkoutRequest))
                        .isInstanceOf(PaymentGatewayException.class)
                        .hasMessageContaining(referenceId.toString());
            }
        }

        @Test
        @DisplayName("deve lançar PaymentGatewayException quando MPException for lançada")
        void shouldThrowPaymentGatewayException_whenMPException() throws MPException, MPApiException {
            MPException mpException = new MPException("Falha interna do SDK - TESTE");

            try (MockedConstruction<PreferenceClient> mockedClient =
                         mockConstruction(PreferenceClient.class, (mock, ctx) ->
                                 when(mock.create(any())).thenThrow(mpException))) {

                assertThatThrownBy(() -> mercadoPagoClient.createPreference(checkoutRequest))
                        .isInstanceOf(PaymentGatewayException.class)
                        .hasMessageContaining(referenceId.toString());
            }
        }
    }

    @Nested
    @DisplayName("getPaymentStatus()")
    final class GetPaymentStatus {
        @Test
        @DisplayName("deve retornar MercadoPagoPaymentResult quando pagamento for encontrado")
        void shouldReturnPaymentResult_whenPaymentExists() throws MPException, MPApiException {
            long paymentId = 123456789L;

            PaymentOrder order = mock(PaymentOrder.class);
            when(order.getId()).thenReturn(987654321L);

            Payment payment = mock(Payment.class);
            when(payment.getId()).thenReturn(paymentId);
            when(payment.getOrder()).thenReturn(order);
            when(payment.getExternalReference()).thenReturn(referenceId.toString());
            when(payment.getStatus()).thenReturn("approved");
            when(payment.getStatusDetail()).thenReturn("accredited");
            when(payment.getInstallments()).thenReturn(1);
            when(payment.getPaymentMethodId()).thenReturn("pix");
            when(payment.getPaymentTypeId()).thenReturn("pix");
            when(payment.getDateApproved()).thenReturn(OffsetDateTime.now());

            try (MockedConstruction<PaymentClient> mockedClient =
                         mockConstruction(PaymentClient.class, (mock, ctx) ->
                                 when(mock.get(paymentId)).thenReturn(payment))) {

                MercadoPagoPaymentResult result =
                        mercadoPagoClient.getPaymentStatus(paymentId);

                assertThat(result.id()).isEqualTo(String.valueOf(paymentId));
                assertThat(result.orderId()).isEqualTo("987654321");
                assertThat(result.externalReference()).isEqualTo(referenceId.toString());
                assertThat(result.status()).isEqualTo("approved");
                assertThat(result.statusDetail()).isEqualTo("accredited");
                assertThat(result.paymentType()).isEqualTo("pix");
                assertThat(result.installments()).isEqualTo(1);
            }
        }

        @Test
        @DisplayName("deve lançar PaymentGatewayException quando pagamento retornar null")
        void shouldThrowPaymentGatewayException_whenPaymentIsNull() throws MPException, MPApiException {
            long paymentId = 123456789L;

            try (MockedConstruction<PaymentClient> mockedClient =
                         mockConstruction(PaymentClient.class, (mock, ctx) ->
                                 when(mock.get(paymentId)).thenReturn(null))) {

                assertThatThrownBy(() -> mercadoPagoClient.getPaymentStatus(paymentId))
                        .isInstanceOf(PaymentGatewayException.class)
                        .hasMessageContaining(String.valueOf(paymentId));
            }
        }

        @Test
        @DisplayName("deve lançar PaymentGatewayException quando MPApiException for lançada")
        void shouldThrowPaymentGatewayException_whenMPApiException() throws MPException, MPApiException {
            long paymentId = 123456789L;

            MPApiException apiException = mock(MPApiException.class);
            when(apiException.getStatusCode()).thenReturn(404);
            when(apiException.getApiResponse()).thenReturn(mock(com.mercadopago.net.MPResponse.class));

            try (MockedConstruction<PaymentClient> mockedClient =
                         mockConstruction(PaymentClient.class, (mock, ctx) ->
                                 when(mock.get(paymentId)).thenThrow(apiException))) {

                assertThatThrownBy(() -> mercadoPagoClient.getPaymentStatus(paymentId))
                        .isInstanceOf(PaymentGatewayException.class)
                        .hasMessageContaining(String.valueOf(paymentId));
            }
        }

        @Test
        @DisplayName("deve lançar PaymentGatewayException quando MPException for lançada")
        void shouldThrowPaymentGatewayException_whenMPException() throws MPException, MPApiException {
            long paymentId = 123456789L;
            MPException mpException = new MPException("Falha interna do SDK - TESTE");

            try (MockedConstruction<PaymentClient> mockedClient =
                         mockConstruction(PaymentClient.class, (mock, ctx) ->
                                 when(mock.get(paymentId)).thenThrow(mpException))) {

                assertThatThrownBy(() -> mercadoPagoClient.getPaymentStatus(paymentId))
                        .isInstanceOf(PaymentGatewayException.class)
                        .hasMessageContaining(String.valueOf(paymentId));
            }
        }
    }

    @Nested
    @DisplayName("expirePreference()")
    final class ExpirePreference {
        @Test
        @DisplayName("deve expirar a preferência com sucesso sem lançar exceção")
        void shouldExpirePreference_whenSuccess() throws MPException, MPApiException {
            String preferenceId = "pref_abc123";

            try (MockedConstruction<PreferenceClient> mockedClient =
                         mockConstruction(PreferenceClient.class)) {

                mercadoPagoClient.expirePreference(preferenceId);

                PreferenceClient clientInstanciado = mockedClient.constructed().getFirst();
                verify(clientInstanciado).update(eq(preferenceId), any());
            }
        }

        @Test
        @DisplayName("deve silenciar MPApiException sem propagar (falha não crítica)")
        void shouldSilenceMPApiException_whenFails() throws MPException, MPApiException {
            String preferenceId = "pref_abc123";

            MPApiException apiException = mock(MPApiException.class);
            when(apiException.getMessage()).thenReturn("erro api");

            try (MockedConstruction<PreferenceClient> mockedClient =
                         mockConstruction(PreferenceClient.class, (mock, ctx) ->
                                 doThrow(apiException).when(mock).update(eq(preferenceId), any()))) {

                // Não deve propagar a exceção — comportamento intencional do método
                mercadoPagoClient.expirePreference(preferenceId);
            }
        }

        @Test
        @DisplayName("deve silenciar MPException sem propagar (falha não crítica)")
        void shouldSilenceMPException_whenFails() throws MPException, MPApiException {
            String preferenceId = "pref_abc123";
            MPException mpException = new MPException("Falha SDK");

            try (MockedConstruction<PreferenceClient> mockedClient =
                         mockConstruction(PreferenceClient.class, (mock, ctx) ->
                                 doThrow(mpException).when(mock).update(eq(preferenceId), any()))) {

                // Não deve propagar a exceção — comportamento intencional do método
                mercadoPagoClient.expirePreference(preferenceId);
            }
        }
    }
}
