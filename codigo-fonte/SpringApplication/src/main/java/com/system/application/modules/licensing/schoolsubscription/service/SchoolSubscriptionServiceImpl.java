package com.system.application.modules.licensing.schoolsubscription.service;

import com.system.application.modules.licensing.billingdiscount.service.BillingDiscountService;
import com.system.application.modules.licensing.schoolsubscription.dto.*;
import com.system.application.modules.school.School;
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
    private final UserService userService;
    private final PaymentGateway paymentGateway;

    public SchoolSubscriptionServiceImpl(
            SchoolSubscriptionRepository schoolSubscriptionRepository,
            SchoolPlanService schoolPlanService,
            BillingDiscountService billingDiscountService,
            SchoolPaymentService schoolPaymentService,
            SchoolService schoolService,
            UserService userService,
            @Qualifier("mercadopago") PaymentGateway paymentGateway
    ) {
        this.schoolSubscriptionRepository = schoolSubscriptionRepository;
        this.schoolPlanService = schoolPlanService;
        this.billingDiscountService = billingDiscountService;
        this.schoolPaymentService = schoolPaymentService;
        this.schoolService = schoolService;
        this.userService = userService;
        this.paymentGateway = paymentGateway;
    }

    @Override
    public PageResponse<SchoolSubscriptionResponse> findAllResponseBySchoolId(UUID userId, int page, int size) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando assinaturas da escola. [requisitanteId={}] [schoolId={}] [page={}] [size={}]",
                userId, school.getId(), page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SchoolSubscriptionResponse> response = schoolSubscriptionRepository
                .findBySchoolId(school.getId(), pageable)
                .map(ss -> new SchoolSubscriptionResponse(
                        ss.getId(),
                        ss.getPlanName(),
                        ss.getStartDate(),
                        ss.getEndDate(),
                        ss.getStatus()
                ));

        log.info("Assinaturas da escola encontradas. [schoolId={}] [total={}] [totalPages={}]",
                school.getId(), response.getTotalElements(), response.getTotalPages());

        return PageResponse.from(response);
    }

    @Override
    public SchoolSubscription findById(UUID schoolSubscriptionId) {
        return schoolSubscriptionRepository.findById(schoolSubscriptionId)
                .orElseThrow(() -> {
                    log.warn("Assinatura da escola nao encontrada. [schoolSubscriptionId={}]",
                            schoolSubscriptionId);
                    return new NotFoundObjectException("Nao encontrou a licenca da escola");
                });
    }

    @Override
    public SchoolSubscription findActiveSubscriptionBySchoolId(UUID schoolId) {
        return schoolSubscriptionRepository
                .findBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Escola sem licenca ativa. [schoolId={}]", schoolId);
                    return new SubscriptionException("A escola nao possui uma licenca ativa.");
                });
    }

    @Override
    public SchoolSubscriptionDetailResponse findDetailById(UUID userId, UUID schoolSubscriptionId) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando detalhes da assinatura. [requisitanteId={}] [schoolSubscriptionId={}] [schoolId={}]",
                userId, schoolSubscriptionId, school.getId());

        SchoolSubscription subscription = findById(schoolSubscriptionId);
        ensureSubscriptionBelongsToSchool(school, subscription);

        SchoolPayment payment = schoolPaymentService.findBySchoolSubscriptionId(subscription.getId());

        return SchoolSubscriptionDetailResponse.from(subscription, payment);
    }

    @Override
    public SubscriptionInfoResponse findActiveSubscription(UUID userId) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando assinatura ativa da escola. [requisitanteId={}] [schoolId={}]",
                userId, school.getId());

        return schoolSubscriptionRepository
                .findBySchoolIdAndStatus(school.getId(), SubscriptionStatus.ACTIVE)
                .map(s -> new SubscriptionInfoResponse(
                        s.getPlanName(),
                        s.getMaxStudents(),
                        s.getMaxCollaborators(),
                        s.getMaxLegalGuardian(),
                        s.getMaxSchoolAdmin()
                ))
                .orElseThrow(() -> {
                    log.warn("Escola sem licenca ativa ao buscar info da assinatura. [schoolId={}]", school.getId());
                    return new SubscriptionException("A escola nao possui uma licenca ativa.");
                });
    }

    @Override
    @Transactional
    public SchoolSubscriptionCheckoutResponse create(UUID userId, SchoolSubscriptionRequest request) {
        School school = schoolService.findByUserId(userId);
        User user = userService.findById(userId);

        log.info("Iniciando criacao de assinatura. [requisitanteId={}] [schoolId={}] [schoolPlanId={}] [meses={}]",
                userId, school.getId(), request.schoolPlanId(), request.months());

        SchoolPlan schoolPlan = schoolPlanService.findById(request.schoolPlanId());
        ensureSchoolPlanExistIsActive(schoolPlan);
        ensureSchoolCanSubscribe(school.getId());

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

        String title = "Licenca - " + subscription.getPlanName();
        String description = "Pagamento da licenca " + subscription.getPlanName();

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
            payment.setStatus(PaymentStatus.FAILED);
            log.info("Pagamento marcado como falho por cancelamento de assinatura nao ativa. [schoolSubscriptionId={}] [paymentId={}] [statusAnterior={}]",
                    schoolSubscriptionId, payment.getId(), subscription.getStatus());
        }

        subscription.setStatus(SubscriptionStatus.CANCELED);

        log.info("Assinatura cancelada com sucesso. [schoolSubscriptionId={}] [schoolId={}]",
                schoolSubscriptionId, school.getId());
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

    private void ensureSchoolCanSubscribe(UUID schoolId) {
        boolean hasActiveSubscription =
                schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE);
        if (hasActiveSubscription) {
            log.warn("Escola tentou criar assinatura ja possuindo uma ativa. [schoolId={}]", schoolId);
            throw new BusinessException("A escola já possui uma assinatura ativa. Para contratar um novo plano, cancele o plano atual primeiro.");
        }

        boolean hasPendingSubscription =
                schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.PENDING_PAYMENT);
        if (hasPendingSubscription) {
            log.warn("Escola tentou criar assinatura ja possuindo uma pendente. [schoolId={}]", schoolId);
            throw new BusinessException("A escola já possui uma assinatura pendente. Não é possível criar uma nova licença possuindo uma licença pendente.");
        }
    }
}