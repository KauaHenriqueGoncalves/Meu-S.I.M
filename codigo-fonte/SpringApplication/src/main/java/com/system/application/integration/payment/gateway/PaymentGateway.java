package com.system.application.integration.payment.gateway;

import com.system.application.integration.payment.gateway.dto.CheckoutRequest;
import com.system.application.integration.payment.gateway.dto.CheckoutResponse;

public interface PaymentGateway {
    CheckoutResponse createCheckout(CheckoutRequest request);
}
