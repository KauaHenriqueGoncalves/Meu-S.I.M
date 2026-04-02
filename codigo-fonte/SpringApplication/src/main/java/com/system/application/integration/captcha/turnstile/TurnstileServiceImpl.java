package com.system.application.integration.captcha.turnstile;

import com.system.application.integration.captcha.service.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Qualifier("turnstile")
public class TurnstileServiceImpl implements CaptchaService {
    private static final Logger log =
            LoggerFactory.getLogger(TurnstileServiceImpl.class);

    @Value("${turnstile.secret-key}")
    private String secretKey;

    @Value("${turnstile.verify-url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean validate(String token) {
        log.info("Iniciando validacao de captcha Turnstile.");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secretKey);
        body.add("response", token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(verifyUrl, request, Map.class);
            Map<String, Object> result = response.getBody();

            boolean success = result != null && Boolean.TRUE.equals(result.get("success"));

            if (success) {
                log.info("Validacao de captcha Turnstile bem-sucedida.");
            } else {
                log.warn("Validacao de captcha Turnstile falhou. [errorCodes={}]",
                        result != null ? result.get("error-codes") : "resposta nula");
            }

            return success;
        }
        catch (Exception e) {
            log.error("Erro ao comunicar com o servico Turnstile. [url={}] [motivo={}]",
                    verifyUrl, e.getMessage(), e);
            return false;
        }
    }
}
