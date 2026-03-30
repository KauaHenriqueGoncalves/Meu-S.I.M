package com.system.application.integration.captcha.service;

public interface CaptchaService {
    boolean validate(String token);
}
