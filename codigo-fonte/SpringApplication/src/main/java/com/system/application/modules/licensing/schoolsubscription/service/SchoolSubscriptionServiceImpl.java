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
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            PaymentGateway paymentGateway
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
        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SchoolSubscriptionResponse> response =
                schoolSubscriptionRepository.findBySchoolId(school.getId(), pageable)
                        .map(ss ->
                                new SchoolSubscriptionResponse(
                                        ss.getId(),
                                        ss.getPlanName(),
                                        ss.getStartDate(),
                                        ss.getEndDate(),
                                        ss.getStatus()));
        return PageResponse.from(response);
    }

    @Override
    public SchoolSubscription findById(UUID schoolSubscriptionId) {
        return schoolSubscriptionRepository.findById(schoolSubscriptionId)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou a licença da escola"));
    }

    @Override
    public SchoolSubscriptionDetailResponse findDetailById(UUID userId, UUID schoolSubscriptionId) {
        School school = schoolService.findByUserId(userId);
        SchoolSubscription subscription = findById(schoolSubscriptionId);
        ensureSubscriptionBelongsToSchool(school, subscription);
        SchoolPayment payment = schoolPaymentService.findBySchoolSubscriptionId(subscription.getId());
        return SchoolSubscriptionDetailResponse.from(subscription, payment);
    }

    @Override
    @Transactional
    public SchoolSubscriptionCheckoutResponse create(UUID userId, SchoolSubscriptionRequest request) {
        School school = schoolService.findByUserId(userId);
        SchoolPlan schoolPlan = schoolPlanService.findById(request.schoolPlanId());
        User user = userService.findById(userId);

        ensureSchoolPlanExistIsActive(schoolPlan);
        ensureSchoolCanSubscribe(school.getId());

        BigDecimal discountForMonth = billingDiscountService.findBestDiscountFor(request.months());
        BigDecimal basePrice = schoolPlan.getMonthlyPrice().multiply(BigDecimal.valueOf(request.months()));
        BigDecimal finalPrice = basePrice.subtract(basePrice.multiply(discountForMonth)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

        SchoolSubscription subscription = schoolSubscriptionRepository.save(
                SchoolSubscription.create(
                        school,
                        schoolPlan,
                        request.months(),
                        finalPrice,
                        SubscriptionStatus.PENDING_PAYMENT
                )
        );

        String title = "Licença - " + subscription.getPlanName();
        String description = "Pagemento da licença " + subscription.getPlanName();
        int installments = request.months();

        CheckoutRequest checkoutRequest = new CheckoutRequest(
                subscription.getId(),
                title,
                description,
                subscription.getPlanPrice(),
                installments,
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
        SchoolSubscription subscription = findById(schoolSubscriptionId);
        SchoolPayment payment = schoolPaymentService.findBySchoolSubscriptionId(subscription.getId());

        if (payment.getStatus().equals(PaymentStatus.PAID)) {
            log.warn("Pagamento duplicado detectado: orderId={}, paymentId={}. Passível de reembolso.",
                    paymentResult.orderId(), payment.getId());
            return null;
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        payment.setPaymentMethod(mapPaymentMethod(paymentResult.paymentTypeId()));
        payment.setPaymentType(paymentResult.paymentTypeId());
        payment.setInstallments(paymentResult.installments());
        payment.setOrderId(paymentResult.orderId());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(paymentResult.paidAt().toInstant());

        log.info("Subscription {} activated. Payment {} confirmed.", subscription.getId(), payment.getId());

        return payment.getProviderPaymentId();
    }

    @Override
    @Transactional
    public void cancelById(UUID userId, UUID schoolSubscriptionId) {
        School school = schoolService.findByUserId(userId);
        SchoolSubscription subscription = findById(schoolSubscriptionId);
        ensureSubscriptionBelongsToSchool(school, subscription);
        if (!(subscription.getStatus() == SubscriptionStatus.ACTIVE)) {
            SchoolPayment payment = schoolPaymentService.findBySchoolSubscriptionId(subscription.getId());
            payment.setStatus(PaymentStatus.FAILED);
        }
        subscription.setStatus(SubscriptionStatus.CANCELED);
    }

    private PaymentMethod mapPaymentMethod(String paymentTypeId) {
        return switch (paymentTypeId) {
            case "pix", "bank_transfer" -> PaymentMethod.PIX;
            case "credit_card"          -> PaymentMethod.CREDIT_CARD;
            case "debit_card"           -> PaymentMethod.DEBIT_CARD;
            case "ticket"               -> PaymentMethod.BOLETO;
            default -> throw new BusinessException(
                    "Método de pagamento não suportado: " + paymentTypeId);
        };
    }

    private void ensureSubscriptionBelongsToSchool(School school, SchoolSubscription subscription) {
        if (!subscription.getSchool().getId().equals(school.getId())) {
            throw new AccessDeniedException(
                    "Não é possivel interagir com funcionalidades de outra escola");
        }
    }

    private void ensureSchoolPlanExistIsActive(SchoolPlan schoolPlan) {
        if (!schoolPlan.getActive()) {
            throw new BusinessException("Plano escolar deve ser ativo");
        }
    }

    private void ensureSchoolCanSubscribe(UUID schoolId) {
        boolean hasActiveSubscription =
                schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE);
        if (hasActiveSubscription) {
            throw new BusinessException(
                    "A escola já possui uma assinatura ativa. Para contratar um novo plano, cancele o plano atual primeiro.");
        }

        boolean hasPendingSubscription =
                schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.PENDING_PAYMENT);
        if (hasPendingSubscription) {
            throw new BusinessException(
                    "A escola já possui uma assinatura pendente. Não é possível criar uma nova licença possuindo uma licença pendente.");
        }
    }
}