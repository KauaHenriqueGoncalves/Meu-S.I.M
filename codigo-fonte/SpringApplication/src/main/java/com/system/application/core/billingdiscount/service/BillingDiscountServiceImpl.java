package com.system.application.core.billingdiscount.service;

import com.system.application.core.billingdiscount.BillingDiscount;
import com.system.application.core.billingdiscount.dto.BillingDiscountRequest;
import com.system.application.core.billingdiscount.dto.BillingDiscountResponse;
import com.system.application.core.billingdiscount.repository.BillingDiscountRepository;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BillingDiscountServiceImpl implements BillingDiscountService {
    private final BillingDiscountRepository billingDiscountRepository;

    public BillingDiscountServiceImpl(
            BillingDiscountRepository billingDiscountRepository
    ) {
        this.billingDiscountRepository = billingDiscountRepository;
    }

    @Override
    public List<BillingDiscountResponse> findAll() {
        return billingDiscountRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(BillingDiscount::getMonths))
                .map(bd ->
                        new BillingDiscountResponse(bd.getId(), bd.getMonths(), bd.getDiscountPercent()))
                .toList();
    }

    @Override
    public BillingDiscount findById(UUID id) {
        return billingDiscountRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou o desconto"));
    }

    @Override
    public BigDecimal findBestDiscountFor(Integer months) {
        return billingDiscountRepository
                .findFirstByMonthsLessThanEqualOrderByMonthsDesc(months)
                .map(BillingDiscount::getDiscountPercent)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public BillingDiscount save(BillingDiscountRequest request) {
        BigDecimal percent = normalizePercent(request.discountPercent());
        BillingDiscount billingDiscount =
                new BillingDiscount(null, request.months(), percent);
        ensureNotExistTheSameBillingDiscount(request.months(), null);
        billingDiscount = billingDiscountRepository.save(billingDiscount);
        return billingDiscount;
    }

    @Override
    @Transactional
    public void update(UUID id, BillingDiscountRequest request) {
        BillingDiscount billingDiscount = findById(id);
        ensureNotExistTheSameBillingDiscount(request.months(), id);
        billingDiscount.setMonths(request.months());
        billingDiscount.setDiscountPercent(normalizePercent(request.discountPercent()));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        billingDiscountRepository.deleteById(id);
    }

    private BigDecimal normalizePercent(BigDecimal percent) {
        return percent.setScale(2, RoundingMode.HALF_UP);
    }

    private void ensureNotExistTheSameBillingDiscount(Integer months, UUID id) {
        Optional<BillingDiscount> billingDiscountOptional =
                billingDiscountRepository.findByMonthsAndIdNot(months, id);
        if (billingDiscountOptional.isPresent()) {
            throw new BusinessException("Já existe um desconto para essa quantidade de meses");
        }
    }
}
