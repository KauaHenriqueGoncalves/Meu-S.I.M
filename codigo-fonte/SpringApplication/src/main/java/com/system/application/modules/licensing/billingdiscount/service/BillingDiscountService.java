package com.system.application.modules.licensing.billingdiscount.service;

import com.system.application.modules.licensing.billingdiscount.BillingDiscount;
import com.system.application.modules.licensing.billingdiscount.dto.BillingDiscountRequest;
import com.system.application.modules.licensing.billingdiscount.dto.BillingDiscountResponse;
import com.system.application.modules.licensing.billingdiscount.dto.BillingDiscountToClientResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BillingDiscountService {
    List<BillingDiscountResponse> findAll();
    List<BillingDiscountToClientResponseDto> findAllToClient();
    BillingDiscount findById(UUID id);
    BigDecimal findBestDiscountFor(Integer months);
    BillingDiscount save(BillingDiscountRequest request);
    void update(UUID id, BillingDiscountRequest request);
    void deleteById(UUID id);
}
