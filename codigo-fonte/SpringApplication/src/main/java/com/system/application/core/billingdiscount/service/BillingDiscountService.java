package com.system.application.core.billingdiscount.service;

import com.system.application.core.billingdiscount.BillingDiscount;
import com.system.application.core.billingdiscount.dto.BillingDiscountRequest;
import com.system.application.core.billingdiscount.dto.BillingDiscountResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BillingDiscountService {
    List<BillingDiscountResponse> findAll();
    BillingDiscount findById(UUID id);
    BigDecimal findBestDiscountFor(Integer months);
    BillingDiscount save(BillingDiscountRequest request);
    void update(UUID id, BillingDiscountRequest request);
    void deleteById(UUID id);
}
