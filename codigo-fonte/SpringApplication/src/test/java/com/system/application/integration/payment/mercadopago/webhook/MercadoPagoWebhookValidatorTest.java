package com.system.application.integration.payment.mercadopago.webhook;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MercadoPagoWebhookValidator")
public class MercadoPagoWebhookValidatorTest {
    @InjectMocks
    private MercadoPagoWebhookValidator validator;

    private static final String SECRET    = "minha-chave-secreta-teste";
    private static final String DATA_ID   = "123456789";
    private static final String REQUEST_ID = "req-abc-123";
    private static final String TS        = "1700000000";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(validator, "webhookSecret", SECRET);
    }

    private String generateHmac(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
        );
        mac.init(keySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02x", b));
        return hex.toString();
    }

    private String buildManifest(String dataId, String requestId, String ts) {
        return "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";
    }

    private HttpServletRequest mockRequest(String xSignature, String xRequestId) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("x-signature")).thenReturn(xSignature);
        when(request.getHeader("x-request-id")).thenReturn(xRequestId);
        return request;
    }

    @Nested
    @DisplayName("isValid() — assinatura válida")
    final class ValidSignature {
        @Test
        @DisplayName("deve retornar true quando assinatura HMAC for correta")
        void shouldReturnTrue_whenSignatureIsCorrect() throws Exception {
            String manifest = buildManifest(DATA_ID, REQUEST_ID, TS);
            String validV1  = generateHmac(manifest, SECRET);

            String xSignature = "ts=" + TS + ",v1=" + validV1;
            HttpServletRequest request = mockRequest(xSignature, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isTrue();
        }

        @Test
        @DisplayName("deve retornar true com campos na ordem inversa no x-signature")
        void shouldReturnTrue_whenXSignatureFieldsAreReversed() throws Exception {
            String manifest = buildManifest(DATA_ID, REQUEST_ID, TS);
            String validV1  = generateHmac(manifest, SECRET);

            // v1 antes de ts
            String xSignature = "v1=" + validV1 + ",ts=" + TS;
            HttpServletRequest request = mockRequest(xSignature, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isTrue();
        }
    }

    @Nested
    @DisplayName("isValid() — headers ausentes")
    final class MissingHeaders {
        @Test
        @DisplayName("deve retornar false quando x-signature for null")
        void shouldReturnFalse_whenXSignatureIsNull() {
            HttpServletRequest request = mockRequest(null, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando x-request-id for null")
        void shouldReturnFalse_whenXRequestIdIsNull() {
            HttpServletRequest request = mockRequest("ts=" + TS + ",v1=qualquer", null);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando ambos os headers forem null")
        void shouldReturnFalse_whenBothHeadersAreNull() {
            HttpServletRequest request = mockRequest(null, null);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }
    }

    @Nested
    @DisplayName("isValid() — x-signature malformado")
    final class MalformedXSignature {
        @Test
        @DisplayName("deve retornar false quando x-signature não tiver campo ts")
        void shouldReturnFalse_whenTsIsMissing() {
            // Apenas v1, sem ts
            String xSignature = "v1=algumhash";
            HttpServletRequest request = mockRequest(xSignature, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando x-signature não tiver campo v1")
        void shouldReturnFalse_whenV1IsMissing() {
            // Apenas ts, sem v1
            String xSignature = "ts=" + TS;
            HttpServletRequest request = mockRequest(xSignature, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando x-signature estiver vazio")
        void shouldReturnFalse_whenXSignatureIsEmpty() {
            HttpServletRequest request = mockRequest("", REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }
    }

    @Nested
    @DisplayName("isValid() — assinatura incorreta")
    final class InvalidSignature {
        @Test
        @DisplayName("deve retornar false quando v1 for um hash incorreto")
        void shouldReturnFalse_whenV1IsWrongHash() {
            String xSignature = "ts=" + TS + ",v1=hashincorretoquenaoconferece";
            HttpServletRequest request = mockRequest(xSignature, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando dataId for diferente do usado para gerar o hash")
        void shouldReturnFalse_whenDataIdIsDifferent() throws Exception {
            String manifest = buildManifest("outro-data-id", REQUEST_ID, TS);
            String validV1  = generateHmac(manifest, SECRET);

            String xSignature = "ts=" + TS + ",v1=" + validV1;
            // Passa DATA_ID diferente do usado para gerar o hash
            HttpServletRequest request = mockRequest(xSignature, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando x-request-id for diferente do usado para gerar o hash")
        void shouldReturnFalse_whenRequestIdIsDifferent() throws Exception {
            String manifest = buildManifest(DATA_ID, "outro-request-id", TS);
            String validV1  = generateHmac(manifest, SECRET);

            String xSignature = "ts=" + TS + ",v1=" + validV1;
            // Passa REQUEST_ID diferente do usado para gerar o hash
            HttpServletRequest request = mockRequest(xSignature, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando ts for diferente do usado para gerar o hash")
        void shouldReturnFalse_whenTsIsDifferent() throws Exception {
            String manifest = buildManifest(DATA_ID, REQUEST_ID, "9999999999");
            String validV1  = generateHmac(manifest, SECRET);

            // ts no header é diferente do ts usado para gerar o hash
            String xSignature = "ts=" + TS + ",v1=" + validV1;
            HttpServletRequest request = mockRequest(xSignature, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando secret for diferente do configurado")
        void shouldReturnFalse_whenSecretIsDifferent() throws Exception {
            String manifest = buildManifest(DATA_ID, REQUEST_ID, TS);
            String validV1  = generateHmac(manifest, "chave-errada");

            String xSignature = "ts=" + TS + ",v1=" + validV1;
            HttpServletRequest request = mockRequest(xSignature, REQUEST_ID);

            assertThat(validator.isValid(request, DATA_ID)).isFalse();
        }
    }
}
