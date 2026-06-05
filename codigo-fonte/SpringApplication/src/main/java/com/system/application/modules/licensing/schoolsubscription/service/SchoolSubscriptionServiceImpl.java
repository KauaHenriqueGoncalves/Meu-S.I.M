package com.system.application.modules.licensing.schoolsubscription.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.modules.licensing.billingdiscount.service.BillingDiscountService;
import com.system.application.modules.licensing.schoolsubscription.dto.*;
import com.system.application.modules.school.School;
import com.system.application.modules.school.dto.SchoolCapacityResponseDTO;
import com.system.application.modules.school.service.SchoolCapacityQuery;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.modules.licensing.schoolpayment.SchoolPayment;
import com.system.application.modules.licensing.schoolpayment.dto.SchoolPaymentRequest;
import com.system.application.modules.licensing.schoolpayment.enums.PaymentMethod;
import com.system.application.modules.licensing.schoolpayment.enums.PaymentStatus;
import com.system.application.modules.licensing.schoolpayment.service.SchoolPaymentService;
import com.system.application.modules.licensing.schoolplan.SchoolPlan;
import com.system.application.modules.licensing.schoolplan.service.SchoolPlanService;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.enums.SubscriptionStatus;
import com.system.application.modules.licensing.schoolsubscription.repository.SchoolSubscriptionRepository;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.service.UserService;
import com.system.application.integration.payment.gateway.PaymentGateway;
import com.system.application.integration.payment.gateway.dto.CheckoutRequest;
import com.system.application.integration.payment.gateway.dto.CheckoutResponse;
import com.system.application.integration.payment.gateway.dto.PayerInfo;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.exception.SubscriptionException;
import com.system.application.shared.services.cache.CacheService;
import com.system.application.shared.services.cache.keys.CacheKeys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class SchoolSubscriptionServiceImpl implements SchoolSubscriptionService {
    private static final Logger log =
            LoggerFactory.getLogger(SchoolSubscriptionServiceImpl.class);

    private final SchoolSubscriptionRepository schoolSubscriptionRepository;
    private final SchoolPlanService schoolPlanService;
    private final BillingDiscountService billingDiscountService;
    private final SchoolPaymentService schoolPaymentService;
    private final SchoolService schoolService;
    private final SchoolCapacityQuery schoolCapacityQuery;
    private final UserService userService;
    private final CacheService cacheService;
    private final PaymentGateway paymentGateway;

    private static final Duration SUBSCRIPTION_TTL = Duration.ofHours(80);

    public SchoolSubscriptionServiceImpl(
            SchoolSubscriptionRepository schoolSubscriptionRepository,
            SchoolPlanService schoolPlanService,
            BillingDiscountService billingDiscountService,
            SchoolPaymentService schoolPaymentService,
            SchoolService schoolService,
            SchoolCapacityQuery schoolCapacityQuery,
            UserService userService,
            CacheService cacheService,
            @Qualifier("mercadopago") PaymentGateway paymentGateway
    ) {
        this.schoolSubscriptionRepository = schoolSubscriptionRepository;
        this.schoolPlanService = schoolPlanService;
        this.billingDiscountService = billingDiscountService;
        this.schoolPaymentService = schoolPaymentService;
        this.schoolService = schoolService;
        this.schoolCapacityQuery = schoolCapacityQuery;
        this.userService = userService;
        this.cacheService = cacheService;
        this.paymentGateway = paymentGateway;
    }

    @Override
    public PageResponse<SchoolSubscriptionResponse> findAllResponseBySchoolId(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando assinaturas da escola. [requisitanteId={}] [schoolId={}] [page={}] [size={}]",
                userId, school.getId(), page, size);

        String key = CacheKeys.subscription(school.getId(), page, size);

        Optional<PageResponse<SchoolSubscriptionResponse>> cacheResponse = cacheService.get(
                key,
                new TypeReference<>() {}
        );

        if (cacheResponse.isPresent()) {
            log.info("Assinaturas da escola encontradas no cache. [schoolId={}] [total={}] [totalPages={}]",
                    school.getId(), cacheResponse.get().totalElements(), cacheResponse.get().totalPages());
            return cacheResponse.get();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<SchoolSubscriptionResponse> responsePage = schoolSubscriptionRepository
                .findBySchoolId(school.getId(), pageable)
                .map(SchoolSubscriptionResponse::of);

        log.info("Assinaturas da escola encontradas. [schoolId={}] [total={}] [totalPages={}]",
                school.getId(), responsePage.getTotalElements(), responsePage.getTotalPages());

        PageResponse<SchoolSubscriptionResponse> response = PageResponse.from(responsePage);

        cacheService.set(key, response, SUBSCRIPTION_TTL);

        return response;
    }

    @Override
    public SchoolSubscription findById(UUID schoolSubscriptionId) {
        log.info("Procurando subscription por ID. [subscriptionId={}]", schoolSubscriptionId);

        return schoolSubscriptionRepository.findById(schoolSubscriptionId)
                .orElseThrow(() -> {
                    log.warn("Assinatura da escola nao encontrada. [schoolSubscriptionId={}]",
                            schoolSubscriptionId);
                    return new NotFoundObjectException("Nao encontrou a licença da escola");
                });
    }

    @Override
    public SchoolSubscription findActiveSubscriptionBySchoolId(UUID schoolId) {
        log.info("Procurando subscription ativo pelo ID do reforco. [schoolId={}]", schoolId);

        return schoolSubscriptionRepository
                .findBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Escola sem licenca ativa. [schoolId={}]", schoolId);
                    return new SubscriptionException("A escola não possui uma licença ativa.");
                });
    }

    @Override
    public SchoolSubscriptionDetailResponse findDetailById(UUID userId, UUID schoolSubscriptionId) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando detalhes da assinatura. [requisitanteId={}] [schoolSubscriptionId={}] [schoolId={}]",
                userId, schoolSubscriptionId, school.getId());

        String key = CacheKeys.subscription(school.getId(), schoolSubscriptionId.toString() + "::detail");

        Optional<SchoolSubscriptionDetailResponse> cacheResponse = cacheService.get(
                key,
                new TypeReference<>() {}
        );

        if (cacheResponse.isPresent()) {
            log.info("Detalhes da assinatura da escola encontradas no cache. [schoolId={}] [subscriptionID={}]",
                    school.getId(), cacheResponse.get().id());
            return cacheResponse.get();
        }

        SchoolSubscription subscription = findById(schoolSubscriptionId);
        ensureSubscriptionBelongsToSchool(school, subscription);

        SchoolPayment payment = schoolPaymentService.findBySchoolSubscriptionId(subscription.getId());

        log.info("Detalhes da assinatura da escola encontradas. [schoolId={}] [subscriptionID={}]",
                school.getId(), payment.getId());

        SchoolSubscriptionDetailResponse response = SchoolSubscriptionDetailResponse.from(subscription, payment);

        cacheService.set(key, response, SUBSCRIPTION_TTL);

        return response;
    }

    @Override
    public SubscriptionInfoResponse findActiveSubscription(UUID userId) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando assinatura ativa da escola. [requisitanteId={}] [schoolId={}]",
                userId, school.getId());

        String key = CacheKeys.subscription(school.getId(), "active");

        Optional<SubscriptionInfoResponse> cacheResponse = cacheService.get(
                key,
                new TypeReference<>() {}
        );

        if (cacheResponse.isPresent()) {
            log.info("Assinatura ativa encontrada no cache. [requisitanteId={}] [schoolId={}]",
                    userId, school.getId());
            return cacheResponse.get();
        }

        SubscriptionInfoResponse response = schoolSubscriptionRepository
                .findBySchoolIdAndStatus(school.getId(), SubscriptionStatus.ACTIVE)
                .map(SubscriptionInfoResponse::of)
                .orElseThrow(() -> {
                    log.warn("Escola sem licenca ativa ao buscar info da assinatura. [schoolId={}]", school.getId());
                    return new SubscriptionException("A escola nao possui uma licença ativa.");
                });

        cacheService.set(key, response, Duration.ofHours(1));

        return response;
    }

    @Override
    @Transactional
    public SchoolSubscriptionCheckoutResponse createCheckout(UUID userId, SchoolSubscriptionRequest request) {
        School school = schoolService.findByUserId(userId);
        User user = userService.findById(userId);

        log.info("Iniciando criacao de assinatura. [requisitanteId={}] [schoolId={}] [schoolPlanId={}] [meses={}]",
                userId, school.getId(), request.schoolPlanId(), request.months());

        SchoolPlan schoolPlan = schoolPlanService.findById(request.schoolPlanId());
        ensureSchoolPlanExistIsActive(schoolPlan);
        ensureSchoolCanSubscribe(school.getId());
        ensureSchoolFitsPlanLimits(school, schoolPlan);

        BigDecimal discountForMonth = billingDiscountService.findBestDiscountFor(request.months());
        BigDecimal basePrice = schoolPlan.getMonthlyPrice().multiply(BigDecimal.valueOf(request.months()));
        BigDecimal finalPrice = basePrice.subtract(
                basePrice.multiply(discountForMonth).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        );

        log.info("Calculo de preco da assinatura. [schoolId={}] [precoBase={}] [desconto={}] [precoFinal={}]",
                school.getId(), basePrice, discountForMonth, finalPrice);

        SchoolSubscription subscription = schoolSubscriptionRepository.save(
                SchoolSubscription.create(
                        school,
                        schoolPlan,
                        request.months(),
                        finalPrice,
                        SubscriptionStatus.PENDING_PAYMENT
                )
        );

        log.info("Assinatura criada com status pendente. [schoolSubscriptionId={}] [schoolId={}] [planName={}]",
                subscription.getId(), school.getId(), subscription.getPlanName());

        String title = "Licença - " + subscription.getPlanName();
        String description = "Pagamento da Licença " + subscription.getPlanName();

        CheckoutRequest checkoutRequest = new CheckoutRequest(
                subscription.getId(),
                title,
                description,
                subscription.getPlanPrice(),
                request.months(),
                new PayerInfo(user.getUsername(), user.getEmail())
        );

        CheckoutResponse checkoutResponse = paymentGateway.createCheckout(checkoutRequest);

        schoolPaymentService.create(
                new SchoolPaymentRequest(
                        subscription,
                        basePrice.subtract(finalPrice),
                        basePrice,
                        finalPrice,
                        PaymentStatus.PENDING,
                        checkoutResponse.preferenceId()
                )
        );

        log.info("Checkout da assinatura gerado com sucesso. [schoolSubscriptionId={}] [preferenceId={}] [precoFinal={}]",
                subscription.getId(), checkoutResponse.preferenceId(), finalPrice);

        String key = CacheKeys.subscriptionPattern(school.getId());

        cacheService.evictByPattern(key);

        return new SchoolSubscriptionCheckoutResponse(
                checkoutRequest.title(),
                checkoutRequest.amount(),
                request.months(),
                checkoutResponse.initPoint(),
                checkoutResponse.preferenceId()
        );
    }

    @Override
    @Transactional
    public String activeById(UUID schoolSubscriptionId, PaymentResult paymentResult) {
        log.info("Iniciando ativacao de assinatura. [schoolSubscriptionId={}] [orderId={}]",
                schoolSubscriptionId, paymentResult.orderId());

        SchoolSubscription subscription = findById(schoolSubscriptionId);
        SchoolPayment payment = schoolPaymentService.findBySchoolSubscriptionId(subscription.getId());

        if (payment.getStatus().equals(PaymentStatus.PAID)) {
            log.warn("Pagamento duplicado detectado, possivel reembolso necessario. [schoolSubscriptionId={}] [orderId={}] [paymentId={}]",
                    schoolSubscriptionId, paymentResult.orderId(), payment.getId());
            return null;
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        payment.setPaymentMethod(mapPaymentMethod(paymentResult.paymentTypeId()));
        payment.setPaymentType(paymentResult.paymentTypeId());
        payment.setInstallments(paymentResult.installments());
        payment.setOrderId(paymentResult.orderId());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(paymentResult.paidAt().toInstant());

        log.info("Assinatura ativada e pagamento confirmado. [schoolSubscriptionId={}] [paymentId={}] [metodo={}]",
                subscription.getId(), payment.getId(), payment.getPaymentMethod());

        String key = CacheKeys.subscriptionPattern(subscription.getSchool().getId());

        cacheService.evictByPattern(key);

        return payment.getProviderPaymentId();
    }

    @Override
    @Transactional
    public void cancelById(UUID userId, UUID schoolSubscriptionId) {
        log.info("Iniciando cancelamento de assinatura. [requisitanteId={}] [schoolSubscriptionId={}]",
                userId, schoolSubscriptionId);

        School school = schoolService.findByUserId(userId);
        SchoolSubscription subscription = findById(schoolSubscriptionId);
        ensureSubscriptionBelongsToSchool(school, subscription);

        if (!(subscription.getStatus() == SubscriptionStatus.ACTIVE)) {
            SchoolPayment payment = schoolPaymentService.findBySchoolSubscriptionId(subscription.getId());

            boolean isExpired = subscription.getStatus().equals(SubscriptionStatus.EXPIRED);

            if (isExpired) {
                log.info("Pagamento expirado não pode ser alterado. [schoolSubscriptionId={}] [paymentId={}] [status={}]",
                        schoolSubscriptionId, payment.getId(), subscription.getStatus());
                throw new BusinessException("Pagamento expirado não pode ser alterado");
            }

            boolean isCanceled = subscription.getStatus().equals(SubscriptionStatus.CANCELED);

            if (isCanceled) {
                log.info("Pagamento cancelado não pode ser alterado. [schoolSubscriptionId={}] [paymentId={}] [status={}]",
                        schoolSubscriptionId, payment.getId(), subscription.getStatus());
                throw new BusinessException("Pagamento cancelado não pode ser alterado");
            }

            payment.setStatus(PaymentStatus.FAILED);
            log.info("Pagamento marcado como falho por cancelamento de assinatura nao ativa. [schoolSubscriptionId={}] [paymentId={}] [statusAnterior={}]",
                    schoolSubscriptionId, payment.getId(), subscription.getStatus());
        }

        subscription.setStatus(SubscriptionStatus.CANCELED);

        log.info("Assinatura cancelada com sucesso. [schoolSubscriptionId={}] [schoolId={}]",
                schoolSubscriptionId, school.getId());

        String key = CacheKeys.subscriptionPattern(school.getId());

        cacheService.evictByPattern(key);
    }

    @Override
    @Transactional
    public void reactiveById(UUID userId, UUID schoolSubscriptionId) {
        log.info("Iniciando reativacao da assinatura. [requisitanteId={}] [schoolSubscriptionId={}]",
                userId, schoolSubscriptionId);

        School school = schoolService.findByUserId(userId);
        SchoolSubscription subscription = findById(schoolSubscriptionId);
        ensureSubscriptionBelongsToSchool(school, subscription);
        ensureSchoolCanSubscribe(school.getId());

        if (LocalDate.now().isAfter(subscription.getEndDate())) {
            log.warn("Licenca fora do prazo de ativacao. [schoolSubscriptionId={}] [now={}] [endDate={}]",
                    schoolSubscriptionId, LocalDate.now(), subscription.getEndDate());
            throw new BusinessException("Não foi possível re-ativar uma licença que está fora do prazo válido.");
        }

        if (!subscription.getStatus().equals(SubscriptionStatus.CANCELED)) {
            log.warn("Licenca esta com o status de cancelada, nap pode ser ativada. [schoolSubscriptionId={}] [paymentId={}] [status={}]",
                    schoolSubscriptionId, subscription.getId(), subscription.getStatus());
            throw new BusinessException("Não é possível re-ativar uma licença que não é cancelada");
        }

        SchoolPayment payment = schoolPaymentService.findBySchoolSubscriptionId(subscription.getId());

        if (!(payment.getStatus().equals(PaymentStatus.PAID) || payment.getPaidAt() != null)) {
            log.warn("Licenca nao é ativada por nunca ter sido efetuado o pagamento. [schoolSubscriptionId={}] [paymentId={}] [status={}]",
                    schoolSubscriptionId, payment.getId(), payment.getStatus());
            throw new BusinessException("Não é possivel re-ativar uma licença que não foi paga.");
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        String key = CacheKeys.subscriptionPattern(school.getId());

        cacheService.evictByPattern(key);
    }

    private PaymentMethod mapPaymentMethod(String paymentTypeId) {
        return switch (paymentTypeId) {
            case "pix", "bank_transfer" -> PaymentMethod.PIX;
            case "credit_card"          -> PaymentMethod.CREDIT_CARD;
            case "debit_card"           -> PaymentMethod.DEBIT_CARD;
            case "ticket"               -> PaymentMethod.BOLETO;
            default -> {
                log.warn("Metodo de pagamento nao mapeado recebido. [paymentTypeId={}]", paymentTypeId);
                throw new BusinessException("Metodo de pagamento nao suportado: " + paymentTypeId);
            }
        };
    }

    private void ensureSubscriptionBelongsToSchool(School school, SchoolSubscription subscription) {
        if (!subscription.getSchool().getId().equals(school.getId())) {
            log.warn("Tentativa de acesso a assinatura de outra escola. [schoolSubscriptionId={}] [subscriptionSchoolId={}] [schoolId={}]",
                    subscription.getId(), subscription.getSchool().getId(), school.getId());
            throw new AccessDeniedException("Não é possivel interagir com funcionalidades de outra escola");
        }
    }

    private void ensureSchoolPlanExistIsActive(SchoolPlan schoolPlan) {
        if (!schoolPlan.getActive()) {
            log.warn("Tentativa de assinar plano inativo. [schoolPlanId={}] [planName={}]",
                    schoolPlan.getId(), schoolPlan.getName());
            throw new BusinessException("Plano escolar deve ser ativo");
        }
    }

    private void ensureSchoolFitsPlanLimits(School school, SchoolPlan schoolPlan) {
        SchoolCapacityResponseDTO capacity = schoolCapacityQuery.getCapacity(school.getId());

        if (capacity.schoolAdmins() > schoolPlan.getMaxSchoolAdmin()) {
            log.warn("Plano não corresponde com a capacitadade do plano em administradores [schoolId={}] [planName={}]",
                    schoolPlan.getId(), schoolPlan.getName());
            throw new BusinessException(
                    String.format(
                            "A escola possui %d administradores, mas o plano selecionado permite no máximo %d.",
                            capacity.schoolAdmins(),
                            schoolPlan.getMaxSchoolAdmin()
                    )
            );
        }

        if (capacity.collaborators() > schoolPlan.getMaxCollaborators()) {
            log.warn("Plano não corresponde com a capacitadade do plano em colaboradores [schoolId={}] [planName={}]",
                    schoolPlan.getId(), schoolPlan.getName());
            throw new BusinessException(
                    String.format(
                            "A escola possui %d colaboradores, mas o plano selecionado permite no máximo %d.",
                            capacity.collaborators(),
                            schoolPlan.getMaxCollaborators()
                    )
            );
        }

        if (capacity.legalGuardians() > schoolPlan.getMaxLegalGuardian()) {
            log.warn("Plano não corresponde com a capacitadade do plano em responsaveis [schoolId={}] [planName={}]",
                    schoolPlan.getId(), schoolPlan.getName());
            throw new BusinessException(
                    String.format(
                            "A escola possui %d reponsáveis, mas o plano selecionado permite no máximo %d.",
                            capacity.legalGuardians(),
                            schoolPlan.getMaxLegalGuardian()
                    )
            );
        }

        if (capacity.students() > schoolPlan.getMaxStudents()) {
            log.warn("Plano não corresponde com a capacitadade do plano em estudantes [schoolId={}] [planName={}]",
                    schoolPlan.getId(), schoolPlan.getName());
            throw new BusinessException(
                    String.format(
                            "A escola possui %d estudantes, mas o plano selecionado permite no máximo %d.",
                            capacity.students(),
                            schoolPlan.getMaxStudents()
                    )
            );
        }
    }

    private void ensureSchoolCanSubscribe(UUID schoolId) {
        boolean hasActiveSubscription =
                schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE);
        if (hasActiveSubscription) {
            log.warn("Escola tentou criar/reativar assinatura ja possuindo uma ativa. [schoolId={}]", schoolId);
            throw new BusinessException("A escola já possui uma assinatura ativa. Para contratar ou re-ativar um novo plano, cancele o plano atual primeiro.");
        }
        boolean hasPendingSubscription =
                schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.PENDING_PAYMENT);
        if (hasPendingSubscription) {
            log.warn("Escola tentou criar assinatura ja possuindo uma pendente. [schoolId={}]", schoolId);
            throw new BusinessException("A escola já possui uma assinatura pendente. Não é possível criar uma nova licença possuindo uma licença pendente.");
        }
    }
}