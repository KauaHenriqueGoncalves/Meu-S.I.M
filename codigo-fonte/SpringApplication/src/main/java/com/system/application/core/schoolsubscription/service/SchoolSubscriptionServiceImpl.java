package com.system.application.core.schoolsubscription.service;

import com.system.application.core.billingdiscount.service.BillingDiscountService;
import com.system.application.core.school.School;
import com.system.application.core.school.service.SchoolService;
import com.system.application.core.schoolpayment.SchoolPayment;
import com.system.application.core.schoolpayment.dto.SchoolPaymentRequest;
import com.system.application.core.schoolpayment.enums.PaymentMethod;
import com.system.application.core.schoolpayment.enums.PaymentStatus;
import com.system.application.core.schoolpayment.service.SchoolPaymentService;
import com.system.application.core.schoolplan.SchoolPlan;
import com.system.application.core.schoolplan.service.SchoolPlanService;
import com.system.application.core.schoolsubscription.SchoolSubscription;
import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionDetailResponse;
import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionRequest;
import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionResponse;
import com.system.application.core.schoolsubscription.enums.SubscriptionStatus;
import com.system.application.core.schoolsubscription.repository.SchoolSubscriptionRepository;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class SchoolSubscriptionServiceImpl implements SchoolSubscriptionService {
    private final SchoolSubscriptionRepository schoolSubscriptionRepository;
    private final SchoolPlanService schoolPlanService;
    private final BillingDiscountService billingDiscountService;
    private final SchoolPaymentService schoolPaymentService;
    private final SchoolService schoolService;

    public SchoolSubscriptionServiceImpl(
            SchoolSubscriptionRepository schoolSubscriptionRepository,
            SchoolPlanService schoolPlanService,
            BillingDiscountService billingDiscountService,
            SchoolPaymentService schoolPaymentService,
            SchoolService schoolService
    ) {
        this.schoolSubscriptionRepository = schoolSubscriptionRepository;
        this.schoolPlanService = schoolPlanService;
        this.billingDiscountService = billingDiscountService;
        this.schoolPaymentService = schoolPaymentService;
        this.schoolService = schoolService;
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
    public SchoolSubscription create(UUID userId, SchoolSubscriptionRequest request) {
        School school = schoolService.findByUserId(userId);
        SchoolPlan schoolPlan = schoolPlanService.findById(request.schoolPlanId());
        ensureSchoolPlanExistIsActive(schoolPlan);
        ensureSchoolHasNotActiveSubscription(school.getId());
        BigDecimal discountForMonth = billingDiscountService.findBestDiscountFor(request.months());
        BigDecimal basePrice = schoolPlan.getMonthlyPrice()
                .multiply(BigDecimal.valueOf(request.months()));
        BigDecimal finalPrice = basePrice.subtract(
                basePrice.multiply(discountForMonth)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(request.months());

        System.out.println("school: " + school);
        System.out.println("schoolPlan: " + schoolPlan);
        System.out.println("Discount for month: " + discountForMonth + "%");
        System.out.println("Base price: " + basePrice);
        System.out.println("Final price: " + finalPrice);
        System.out.println("Start date: " + startDate);
        System.out.println("End date: " + endDate);

        SchoolSubscription subscription = SchoolSubscription.create(
                school,
                schoolPlan,
                request.months(),
                finalPrice,
                SubscriptionStatus.PENDING_PAYMENT
        );

        subscription = schoolSubscriptionRepository.save(subscription);

        SchoolPaymentRequest payment = new SchoolPaymentRequest(
                subscription,
                basePrice.subtract(finalPrice),
                basePrice,
                finalPrice,
                PaymentMethod.PIX,
                PaymentStatus.PENDING
        );

        schoolPaymentService.create(payment);

        return subscription;
    }

    @Override
    @Transactional
    public void ActiveById(UUID userId, UUID schoolSubscriptionId) {
        School school = schoolService.findByUserId(userId);
        SchoolSubscription subscription = findById(schoolSubscriptionId);
        ensureSubscriptionBelongsToSchool(school, subscription);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        SchoolPayment payment = schoolPaymentService.findBySchoolSubscriptionId(subscription.getId());
        payment.setPaidAt(Instant.now());
        payment.setStatus(PaymentStatus.PAID);
        payment.setProviderPaymentId("valor aleatorio para teste");
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

    private void ensureSchoolHasNotActiveSubscription(UUID schoolId) {
        boolean hasActiveSubscription =
                schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE);
        if (hasActiveSubscription) {
            throw new BusinessException(
                    "A escola já possui uma assinatura ativa. Para contratar um novo plano, cancele o plano atual primeiro.");
        }
    }
}
