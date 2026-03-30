package com.system.application.integration.captcha.turnstile;

import com.system.application.integration.captcha.service.CaptchaService;
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
    @Value("${turnstile.secret-key}")
    private String secretKey;

    @Value("${turnstile.verify-url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean validate(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secretKey);
        body.add("response", token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(verifyUrl, request, Map.class);

        Map<String, Object> result = response.getBody();

        return result != null && Boolean.TRUE.equals(result.get("success"));
    }
}
