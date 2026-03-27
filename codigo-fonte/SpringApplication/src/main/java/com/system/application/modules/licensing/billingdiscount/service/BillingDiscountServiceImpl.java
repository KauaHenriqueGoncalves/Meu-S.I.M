package com.system.application.modules.licensing.billingdiscount.service;

import com.system.application.modules.licensing.billingdiscount.BillingDiscount;
import com.system.application.modules.licensing.billingdiscount.dto.BillingDiscountRequest;
import com.system.application.modules.licensing.billingdiscount.dto.BillingDiscountResponse;
import com.system.application.modules.licensing.billingdiscount.repository.BillingDiscountRepository;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BillingDiscountServiceImpl implements BillingDiscountService {
    private static final Logger log =
            LoggerFactory.getLogger(BillingDiscountServiceImpl.class);

    private final BillingDiscountRepository billingDiscountRepository;

    public BillingDiscountServiceImpl(
            BillingDiscountRepository billingDiscountRepository
    ) {
        this.billingDiscountRepository = billingDiscountRepository;
    }

    @Override
    public List<BillingDiscountResponse> findAll() {
        log.info("Buscando todos os descontos de cobranca.");

        List<BillingDiscountResponse> response = billingDiscountRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(BillingDiscount::getMonths))
                .map(bd -> new BillingDiscountResponse(bd.getId(), bd.getMonths(), bd.getDiscountPercent()))
                .toList();

        log.info("Descontos de cobranca encontrados. [total={}]", response.size());

        return response;
    }

    @Override
    public BillingDiscount findById(UUID id) {
        return billingDiscountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Desconto de cobranca nao encontrado. [billingDiscountId={}]", id);
                    return new NotFoundObjectException("Nao encontrou o desconto");
                });
    }

    @Override
    public BigDecimal findBestDiscountFor(Integer months) {
        log.info("Buscando melhor desconto disponivel. [meses={}]", months);

        BigDecimal discount = billingDiscountRepository
                .findFirstByMonthsLessThanEqualOrderByMonthsDesc(months)
                .map(BillingDiscount::getDiscountPercent)
                .orElse(BigDecimal.ZERO);

        log.info("Melhor desconto encontrado. [meses={}] [desconto={}]", months, discount);

        return discount;
    }

    @Override
    @Transactional
    public BillingDiscount save(BillingDiscountRequest request) {
        log.info("Iniciando cadastro de desconto de cobranca. [meses={}] [desconto={}]",
                request.months(), request.discountPercent());

        ensureNotExistTheSameBillingDiscount(request.months(), null);

        BigDecimal percent = normalizePercent(request.discountPercent());
        BillingDiscount billingDiscount = new BillingDiscount(null, request.months(), percent);
        billingDiscount = billingDiscountRepository.save(billingDiscount);

        log.info("Desconto de cobranca cadastrado com sucesso. [billingDiscountId={}] [meses={}] [desconto={}]",
                billingDiscount.getId(), billingDiscount.getMonths(), billingDiscount.getDiscountPercent());

        return billingDiscount;
    }

    @Override
    @Transactional
    public void update(UUID id, BillingDiscountRequest request) {
        log.info("Iniciando atualizacao de desconto de cobranca. [billingDiscountId={}] [meses={}] [desconto={}]",
                id, request.months(), request.discountPercent());

        BillingDiscount billingDiscount = findById(id);
        ensureNotExistTheSameBillingDiscount(request.months(), id);

        billingDiscount.setMonths(request.months());
        billingDiscount.setDiscountPercent(normalizePercent(request.discountPercent()));

        log.info("Desconto de cobranca atualizado com sucesso. [billingDiscountId={}] [meses={}] [desconto={}]",
                id, billingDiscount.getMonths(), billingDiscount.getDiscountPercent());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Iniciando exclusao de desconto de cobranca. [billingDiscountId={}]", id);

        billingDiscountRepository.deleteById(id);

        log.info("Desconto de cobranca excluido com sucesso. [billingDiscountId={}]", id);
    }

    private BigDecimal normalizePercent(BigDecimal percent) {
        return percent.setScale(2, RoundingMode.HALF_UP);
    }

    private void ensureNotExistTheSameBillingDiscount(Integer months, UUID id) {
        Optional<BillingDiscount> existing =
                billingDiscountRepository.findByMonthsAndIdNot(months, id);
        if (existing.isPresent()) {
            log.warn("Tentativa de cadastrar desconto duplicado para o mesmo ciclo. [meses={}] [billingDiscountExistenteId={}]",
                    months, existing.get().getId());
            throw new BusinessException("Já existe um desconto para essa quantidade de meses");
        }
    }
}
