package com.meusim.application.integration.captcha.service;

public interface CaptchaService {
    boolean validate(String token);
}
