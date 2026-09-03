package com.meusim.application.integration.payment.gateway;

import com.meusim.application.integration.payment.gateway.dto.CheckoutRequest;
import com.meusim.application.integration.payment.gateway.dto.CheckoutResponse;

public interface PaymentGateway {
    CheckoutResponse createCheckout(CheckoutRequest request);
}
