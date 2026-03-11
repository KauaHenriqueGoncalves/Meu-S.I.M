package com.system.application.integration.payment.mercadopago.webhook;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class MercadoPagoWebhookValidator {

    @Value("${api.v1.mercado.pago.webhook-secret}")
    private String webhookSecret;

    public boolean isValid(HttpServletRequest request, String dataId) {
        String xSignature = request.getHeader("x-signature");
        String xRequestId = request.getHeader("x-request-id");

        if (xSignature == null || xRequestId == null) return false;

        // Extrai ts e v1 do header
        String ts = null;
        String v1 = null;

        for (String part : xSignature.split(",")) {
            String[] kv = part.split("=", 2);
            if ("ts".equals(kv[0]))     ts = kv[1];
            if ("v1".equals(kv[0]))     v1 = kv[1];
        }

        if (ts == null || v1 == null) return false;

        // Monta a string para assinar
        String manifest = "id:" + dataId + ";request-id:" + xRequestId + ";ts:" + ts + ";";

        // Gera o HMAC-SHA256
        String generated = generateHmac(manifest, webhookSecret);

        return generated.equals(v1);
    }

    private String generateHmac(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Converte para hex
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar HMAC", e);
        }
    }
}
