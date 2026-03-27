package com.system.application.integration.payment.mercadopago.service;

import com.system.application.integration.payment.mercadopago.client.MercadoPagoClient;
import com.system.application.integration.payment.mercadopago.dto.MercadoPagoPaymentResult;
import com.system.application.modules.licensing.schoolsubscription.dto.PaymentResult;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.shared.exception.PaymentGatewayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessPaymentNotificationService")
public class ProcessPaymentNotificationServiceTest {
    @Mock private MercadoPagoClient mercadoPagoClient;
    @Mock private SchoolSubscriptionService schoolSubscriptionService;

    @InjectMocks
    private ProcessPaymentNotificationService processPaymentService;

    private UUID subscriptionId;
    private MercadoPagoPaymentResult approvedPayment;
    private MercadoPagoPaymentResult pendingPayment;

    @BeforeEach
    void setUp() {
        subscriptionId = UUID.randomUUID();

        approvedPayment = new MercadoPagoPaymentResult(
                "123456789",
                "order_001",
                subscriptionId.toString(),
                "approved",
                "accredited",
                1,
                "pix",
                "pix",
                OffsetDateTime.now()
        );

        pendingPayment = new MercadoPagoPaymentResult(
                "123456789",
                "order_001",
                subscriptionId.toString(),
                "pending",
                "waiting_transfer",
                1,
                "pix",
                "pix",
                null
        );
    }

    @Nested
    @DisplayName("processPayment() — tipos ignorados")
    final class IgnoredTypes {
        @Test
        @DisplayName("deve ignorar notificação quando tipo não for aceito")
        void shouldIgnore_whenTypeIsNotAccepted() {
            processPaymentService.processPayment(123456789L, "payment.deleted");

            verifyNoInteractions(mercadoPagoClient);
            verifyNoInteractions(schoolSubscriptionService);
        }

        @Test
        @DisplayName("deve ignorar notificação quando tipo for null")
        void shouldIgnore_whenTypeIsNull() {
            processPaymentService.processPayment(123456789L, null);

            verifyNoInteractions(mercadoPagoClient);
            verifyNoInteractions(schoolSubscriptionService);
        }

        @Test
        @DisplayName("deve ignorar notificação quando tipo for vazio")
        void shouldIgnore_whenTypeIsEmpty() {
            processPaymentService.processPayment(123456789L, "");

            verifyNoInteractions(mercadoPagoClient);
            verifyNoInteractions(schoolSubscriptionService);
        }
    }

    @Nested
    @DisplayName("processPayment() — payment.created")
    final class PaymentCreated {
        @Test
        @DisplayName("deve ativar assinatura e expirar preferência quando pagamento for aprovado")
        void shouldActivateSubscriptionAndExpirePreference_whenApproved() {
            when(mercadoPagoClient.getPaymentStatus(123456789L)).thenReturn(approvedPayment);
            when(schoolSubscriptionService.activeById(eq(subscriptionId), any(PaymentResult.class)))
                    .thenReturn("pref_abc123");

            processPaymentService.processPayment(123456789L, "payment.created");

            verify(mercadoPagoClient).getPaymentStatus(123456789L);
            verify(schoolSubscriptionService).activeById(eq(subscriptionId), any(PaymentResult.class));
            verify(mercadoPagoClient).expirePreference("pref_abc123");
        }

        @Test
        @DisplayName("deve passar PaymentResult correto para activeById")
        void shouldPassCorrectPaymentResult_toActiveById() {
            when(mercadoPagoClient.getPaymentStatus(123456789L)).thenReturn(approvedPayment);
            when(schoolSubscriptionService.activeById(any(), any())).thenReturn("pref_abc123");

            processPaymentService.processPayment(123456789L, "payment.created");

            verify(schoolSubscriptionService).activeById(
                    eq(subscriptionId),
                    argThat(result ->
                            result.paymentMethodId().equals("pix") &&
                                    result.paymentTypeId().equals("pix") &&
                                    result.installments() == 1 &&
                                    result.orderId().equals("order_001")
                    )
            );
        }

        @Test
        @DisplayName("deve retornar sem ação quando pagamento não for aprovado")
        void shouldReturnWithoutAction_whenPaymentIsNotApproved() {
            when(mercadoPagoClient.getPaymentStatus(123456789L)).thenReturn(pendingPayment);

            processPaymentService.processPayment(123456789L, "payment.created");

            verify(mercadoPagoClient).getPaymentStatus(123456789L);
            verifyNoInteractions(schoolSubscriptionService);
            verify(mercadoPagoClient, never()).expirePreference(any());
        }

        @Test
        @DisplayName("deve lançar PaymentGatewayException quando externalReference não for UUID válido")
        void shouldThrowPaymentGatewayException_whenExternalReferenceIsInvalidUUID() {
            MercadoPagoPaymentResult invalidRefPayment = new MercadoPagoPaymentResult(
                    "123456789",
                    "order_001",
                    "nao-e-um-uuid",
                    "approved",
                    "accredited",
                    1,
                    "pix",
                    "pix",
                    OffsetDateTime.now()
            );

            when(mercadoPagoClient.getPaymentStatus(123456789L)).thenReturn(invalidRefPayment);

            assertThatThrownBy(() ->
                    processPaymentService.processPayment(123456789L, "payment.created"))
                    .isInstanceOf(PaymentGatewayException.class)
                    .hasMessageContaining("123456789");

            verifyNoInteractions(schoolSubscriptionService);
            verify(mercadoPagoClient, never()).expirePreference(any());
        }
    }

    @Nested
    @DisplayName("processPayment() — payment.updated")
    final class PaymentUpdated {
        @Test
        @DisplayName("deve processar normalmente quando tipo for payment.updated")
        void shouldProcess_whenTypeIsPaymentUpdated() {
            when(mercadoPagoClient.getPaymentStatus(123456789L)).thenReturn(approvedPayment);
            when(schoolSubscriptionService.activeById(eq(subscriptionId), any(PaymentResult.class)))
                    .thenReturn("pref_abc123");

            processPaymentService.processPayment(123456789L, "payment.updated");

            verify(mercadoPagoClient).getPaymentStatus(123456789L);
            verify(schoolSubscriptionService).activeById(eq(subscriptionId), any(PaymentResult.class));
            verify(mercadoPagoClient).expirePreference("pref_abc123");
        }
    }
}
