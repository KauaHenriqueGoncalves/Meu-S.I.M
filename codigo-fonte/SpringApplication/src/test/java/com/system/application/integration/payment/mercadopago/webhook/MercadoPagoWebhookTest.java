package com.system.application.integration.payment.mercadopago.webhook;

import com.system.application.integration.payment.mercadopago.event.PaymentNotificationEvent;
import com.system.application.integration.payment.mercadopago.webhook.dto.MercadoPagoNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("MercadoPagoWebhook")
public class MercadoPagoWebhookTest {
    @Mock
    private MercadoPagoWebhookValidator webhookValidator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MercadoPagoWebhook mercadoPagoWebhook;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private MercadoPagoNotification validNotification;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(mercadoPagoWebhook)
                .build();

        objectMapper = new ObjectMapper();

        validNotification = new MercadoPagoNotification(
                "payment.created",
                "v1",
                new MercadoPagoNotification.DataDto("123456789"),
                "2024-01-01T00:00:00Z",
                1L,
                true,
                "payment",
                123L
        );
    }

    @Nested
    @DisplayName("POST /webhooks/mercado-pago")
    final class PostWebhook {
        @Test
        @DisplayName("deve retornar 200 e publicar evento quando assinatura for válida")
        void shouldReturn200AndPublishEvent_whenSignatureIsValid() throws Exception {
            when(webhookValidator.isValid(any(), eq("123456789"))).thenReturn(true);

            mockMvc.perform(post("/webhooks/mercado-pago")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validNotification)))
                    .andExpect(status().isOk());

            ArgumentCaptor<PaymentNotificationEvent> eventCaptor =
                    ArgumentCaptor.forClass(PaymentNotificationEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());

            PaymentNotificationEvent event = eventCaptor.getValue();
            assertThat(event.resourceId()).isEqualTo(123456789L);
            assertThat(event.resourceType()).isEqualTo("payment.created");
        }

        @Test
        @DisplayName("deve retornar 401 e não publicar evento quando assinatura for inválida")
        void shouldReturn401AndNotPublishEvent_whenSignatureIsInvalid() throws Exception {
            when(webhookValidator.isValid(any(), any())).thenReturn(false);

            mockMvc.perform(post("/webhooks/mercado-pago")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validNotification)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("deve retornar 400 quando action estiver em branco")
        void shouldReturn400_whenActionIsBlank() throws Exception {
            MercadoPagoNotification invalidNotification = new MercadoPagoNotification(
                    "",
                    "v1",
                    new MercadoPagoNotification.DataDto("123456789"),
                    "2024-01-01T00:00:00Z",
                    1L,
                    true,
                    "payment",
                    123L
            );

            mockMvc.perform(post("/webhooks/mercado-pago")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidNotification)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(webhookValidator);
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("deve retornar 400 quando data for null")
        void shouldReturn400_whenDataIsNull() throws Exception {
            MercadoPagoNotification invalidNotification = new MercadoPagoNotification(
                    "payment.created",
                    "v1",
                    null,
                    "2024-01-01T00:00:00Z",
                    1L,
                    true,
                    "payment",
                    123L
            );

            mockMvc.perform(post("/webhooks/mercado-pago")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidNotification)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(webhookValidator);
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("deve retornar 400 quando data.id estiver em branco")
        void shouldReturn400_whenDataIdIsBlank() throws Exception {
            MercadoPagoNotification invalidNotification = new MercadoPagoNotification(
                    "payment.created",
                    "v1",
                    new MercadoPagoNotification.DataDto(""),
                    "2024-01-01T00:00:00Z",
                    1L,
                    true,
                    "payment",
                    123L
            );

            mockMvc.perform(post("/webhooks/mercado-pago")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidNotification)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(webhookValidator);
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("deve retornar 400 quando dateCreated for null")
        void shouldReturn400_whenDateCreatedIsNull() throws Exception {
            MercadoPagoNotification invalidNotification = new MercadoPagoNotification(
                    "payment.created",
                    "v1",
                    new MercadoPagoNotification.DataDto("123456789"),
                    null,
                    1L,
                    true,
                    "payment",
                    123L
            );

            mockMvc.perform(post("/webhooks/mercado-pago")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidNotification)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(webhookValidator);
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("deve retornar 400 quando body for JSON malformado")
        void shouldReturn400_whenBodyIsMalformed() throws Exception {
            mockMvc.perform(post("/webhooks/mercado-pago")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ invalid json }"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(webhookValidator);
            verifyNoInteractions(eventPublisher);
        }
    }

}
