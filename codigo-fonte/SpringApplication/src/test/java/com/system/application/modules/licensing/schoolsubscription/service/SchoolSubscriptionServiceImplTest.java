package com.system.application.modules.licensing.schoolsubscription.service;

import com.system.application.integration.payment.gateway.PaymentGateway;
import com.system.application.integration.payment.gateway.dto.CheckoutRequest;
import com.system.application.integration.payment.gateway.dto.CheckoutResponse;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.service.UserService;
import com.system.application.modules.licensing.billingdiscount.service.BillingDiscountService;
import com.system.application.modules.licensing.schoolpayment.SchoolPayment;
import com.system.application.modules.licensing.schoolpayment.dto.SchoolPaymentRequest;
import com.system.application.modules.licensing.schoolpayment.enums.PaymentMethod;
import com.system.application.modules.licensing.schoolpayment.enums.PaymentStatus;
import com.system.application.modules.licensing.schoolpayment.service.SchoolPaymentService;
import com.system.application.modules.licensing.schoolplan.SchoolPlan;
import com.system.application.modules.licensing.schoolplan.service.SchoolPlanService;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.dto.*;
import com.system.application.modules.licensing.schoolsubscription.enums.SubscriptionStatus;
import com.system.application.modules.licensing.schoolsubscription.repository.SchoolSubscriptionRepository;
import com.system.application.modules.school.School;
import com.system.application.modules.school.dto.SchoolCapacityResponseDTO;
import com.system.application.modules.school.service.SchoolCapacityQuery;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.shared.dto.PageResponse;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.BusinessException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.exception.SubscriptionException;
import com.system.application.shared.services.cache.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SchoolSubscriptionServiceImpl")
public class SchoolSubscriptionServiceImplTest {
    @Mock private SchoolSubscriptionRepository schoolSubscriptionRepository;
    @Mock private SchoolPlanService schoolPlanService;
    @Mock private BillingDiscountService billingDiscountService;
    @Mock private SchoolPaymentService schoolPaymentService;
    @Mock private SchoolService schoolService;
    @Mock private UserService userService;
    @Mock private PaymentGateway paymentGateway;
    @Mock private CacheService cacheService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private SchoolCapacityQuery schoolCapacityQuery;

    @InjectMocks
    private SchoolSubscriptionServiceImpl schoolSubscriptionService;

    private UUID userId;
    private UUID schoolId;
    private UUID subscriptionId;
    private UUID planId;

    private School school;
    private School outraEscola;
    private User user;
    private SchoolPlan schoolPlan;
    private SchoolSubscription subscription;
    private SchoolPayment payment;
    private SchoolCapacityResponseDTO schoolCapacityDtoWithoutUsers ;

    @BeforeEach
    void setUp() {
        userId         = UUID.randomUUID();
        schoolId       = UUID.randomUUID();
        subscriptionId = UUID.randomUUID();
        planId         = UUID.randomUUID();

        school      = new School(schoolId, "escola-01", "Escola Teste", "12345678000195");
        outraEscola = new School(UUID.randomUUID(), "outra-escola", "Outra Escola", "98765432000100");
        schoolCapacityDtoWithoutUsers = new SchoolCapacityResponseDTO(0, 0, 0, 0);

        user = new User(
                userId, "Admin Escola", "admin@escola.com", "hashed",
                "52998224725", "81999990000", "Rua A, 1", true, null, null
        );

        schoolPlan = new SchoolPlan(
                planId, "Plano Básico", BigDecimal.valueOf(100.00),
                50, 10, 20, 5, true
        );

        subscription = new SchoolSubscription(
                subscriptionId, school, schoolPlan, 12, "Plano Básico",
                BigDecimal.valueOf(1200.00), 50, 10, 20, 5,
                LocalDate.now(), LocalDate.now().plusMonths(12),
                SubscriptionStatus.ACTIVE
        );

        payment = new SchoolPayment(
                UUID.randomUUID(), subscription,
                BigDecimal.ZERO, BigDecimal.valueOf(1200.00),
                BigDecimal.valueOf(1200.00), null, null, null, null,
                null, PaymentStatus.PENDING, "pref_123"
        );
    }

    @Nested
    @DisplayName("findById()")
    final class FindById {
        @Test
        @DisplayName("deve retornar a assinatura quando ID existir")
        void shouldReturnSubscription_whenIdExists() {
            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subscription));

            SchoolSubscription result = schoolSubscriptionService.findById(subscriptionId);

            assertThat(result).isEqualTo(subscription);
            verify(schoolSubscriptionRepository).findById(subscriptionId);
        }

        @Test
        @DisplayName("deve lançar NotFoundObjectException quando ID não existir")
        void shouldThrowNotFound_whenIdDoesNotExist() {
            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> schoolSubscriptionService.findById(subscriptionId))
                    .isInstanceOf(NotFoundObjectException.class)
                    .hasMessageContaining("Nao encontrou a licença da escola");
        }
    }

    @Nested
    @DisplayName("findActiveSubscriptionBySchoolId()")
    final class FindActiveSubscriptionBySchoolId {
        @Test
        @DisplayName("deve retornar assinatura ativa quando existir")
        void shouldReturnSubscription_whenActiveExists() {
            when(schoolSubscriptionRepository.findBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.of(subscription));

            SchoolSubscription result =
                    schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId);

            assertThat(result).isEqualTo(subscription);
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando não houver assinatura ativa")
        void shouldThrowSubscription_whenNoActiveExists() {
            when(schoolSubscriptionRepository.findBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId))
                    .isInstanceOf(SubscriptionException.class)
                    .hasMessageContaining("A escola não possui uma licença ativa.");
        }
    }

    @Nested
    @DisplayName("findAllResponseBySchoolId()")
    final class FindAllResponseBySchoolId {
        @Test
        @DisplayName("deve retornar página de assinaturas da escola")
        void shouldReturnPage_whenSchoolHasSubscriptions() {
            Pageable pageable = PageRequest.of(0, 10);

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionRepository.findBySchoolId(eq(schoolId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(subscription), pageable, 1));

            PageResponse<SchoolSubscriptionResponse> result =
                    schoolSubscriptionService.findAllResponseBySchoolId(userId, 0, 10);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().getFirst().planName()).isEqualTo("Plano Básico");
            assertThat(result.content().getFirst().status()).isEqualTo(SubscriptionStatus.ACTIVE);
        }

        @Test
        @DisplayName("deve retornar página vazia quando escola não tiver assinaturas")
        void shouldReturnEmptyPage_whenSchoolHasNoSubscriptions() {
            Pageable pageable = PageRequest.of(0, 10);

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionRepository.findBySchoolId(eq(schoolId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(), pageable, 0));

            PageResponse<SchoolSubscriptionResponse> result =
                    schoolSubscriptionService.findAllResponseBySchoolId(userId, 0, 10);

            assertThat(result.content()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findDetailById()")
    final class FindDetailById {
        @Test
        @DisplayName("deve retornar detalhe da assinatura quando pertencer à escola")
        void shouldReturnDetail_whenSubscriptionBelongsToSchool() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subscription));
            when(schoolPaymentService.findBySchoolSubscriptionId(subscriptionId))
                    .thenReturn(payment);

            SchoolSubscriptionDetailResponse result =
                    schoolSubscriptionService.findDetailById(userId, subscriptionId);

            assertThat(result.id()).isEqualTo(subscriptionId);
            assertThat(result.planName()).isEqualTo("Plano Básico");
            assertThat(result.subscriptionStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando assinatura pertencer a outra escola")
        void shouldThrowAccessDenied_whenSubscriptionBelongsToDifferentSchool() {
            SchoolSubscription subDeOutraEscola = new SchoolSubscription(
                    subscriptionId, outraEscola, schoolPlan, 12, "Plano Básico",
                    BigDecimal.valueOf(1200.00), 50, 10, 20, 5,
                    LocalDate.now(), LocalDate.now().plusMonths(12),
                    SubscriptionStatus.ACTIVE
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subDeOutraEscola));

            assertThatThrownBy(() ->
                    schoolSubscriptionService.findDetailById(userId, subscriptionId))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("findActiveSubscription()")
    final class FindActiveSubscription {
        @Test
        @DisplayName("deve retornar SubscriptionInfoResponse quando escola tiver assinatura ativa")
        void shouldReturnInfo_whenActiveSubscriptionExists() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionRepository.findBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.of(subscription));

            SubscriptionInfoResponse result =
                    schoolSubscriptionService.findActiveSubscription(userId);

            assertThat(result.planName()).isEqualTo("Plano Básico");
            assertThat(result.maxStudents()).isEqualTo(50);
            assertThat(result.maxCollaborators()).isEqualTo(10);
            assertThat(result.maxLegalGuardian()).isEqualTo(20);
            assertThat(result.maxSchoolAdmin()).isEqualTo(5);
        }

        @Test
        @DisplayName("deve lançar SubscriptionException quando escola não tiver assinatura ativa")
        void shouldThrowSubscription_whenNoActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionRepository.findBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    schoolSubscriptionService.findActiveSubscription(userId))
                    .isInstanceOf(SubscriptionException.class);
        }
    }

    @Nested
    @DisplayName("create()")
    final class Create {
        private SchoolSubscriptionRequest request;

        @BeforeEach
        void setUp() {
            request = new SchoolSubscriptionRequest(planId, 12);
        }

        @Test
        @DisplayName("deve criar assinatura e retornar checkout com sucesso")
        void shouldCreateSubscriptionAndReturnCheckout_whenValid() {
            SchoolSubscription pendingSubscription = new SchoolSubscription(
                    subscriptionId, school, schoolPlan, 12, "Plano Básico",
                    BigDecimal.valueOf(1200.00), 50, 10, 20, 5,
                    LocalDate.now(), LocalDate.now().plusMonths(12),
                    SubscriptionStatus.PENDING_PAYMENT
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(userService.findById(userId)).thenReturn(user);
            when(schoolPlanService.findById(planId)).thenReturn(schoolPlan);
            when(schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE))
                    .thenReturn(false);
            when(schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.PENDING_PAYMENT))
                    .thenReturn(false);
            when(billingDiscountService.findBestDiscountFor(12))
                    .thenReturn(BigDecimal.ZERO);
            when(schoolCapacityQuery.getCapacity(schoolId))
                    .thenReturn(schoolCapacityDtoWithoutUsers);
            when(schoolSubscriptionRepository.save(any(SchoolSubscription.class)))
                    .thenReturn(pendingSubscription);
            when(paymentGateway.createCheckout(any(CheckoutRequest.class)))
                    .thenReturn(new CheckoutResponse("pref_123", "https://init.point"));

            SchoolSubscriptionCheckoutResponse result =
                    schoolSubscriptionService.createCheckout(userId, request);

            assertThat(result.preferenceId()).isEqualTo("pref_123");
            assertThat(result.initPoint()).isEqualTo("https://init.point");
            assertThat(result.months()).isEqualTo(12);
            verify(schoolSubscriptionRepository).save(any(SchoolSubscription.class));
            verify(schoolPaymentService).create(any(SchoolPaymentRequest.class));
        }

        @Test
        @DisplayName("deve aplicar desconto corretamente no preço final")
        void shouldApplyDiscount_whenDiscountExists() {
            // 12 meses x R$100,00 = R$1200,00 com 10% de desconto = R$1080,00
            SchoolSubscription pendingSubscription = new SchoolSubscription(
                    subscriptionId, school, schoolPlan, 12, "Plano Básico",
                    BigDecimal.valueOf(1080.00), 50, 10, 20, 5,
                    LocalDate.now(), LocalDate.now().plusMonths(12),
                    SubscriptionStatus.PENDING_PAYMENT
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(userService.findById(userId)).thenReturn(user);
            when(schoolPlanService.findById(planId)).thenReturn(schoolPlan);
            when(schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE))
                    .thenReturn(false);
            when(schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.PENDING_PAYMENT))
                    .thenReturn(false);
            when(billingDiscountService.findBestDiscountFor(12))
                    .thenReturn(BigDecimal.valueOf(10)); // 10% de desconto
            when(schoolCapacityQuery.getCapacity(schoolId))
                    .thenReturn(schoolCapacityDtoWithoutUsers);
            when(schoolSubscriptionRepository.save(any(SchoolSubscription.class)))
                    .thenReturn(pendingSubscription);
            when(paymentGateway.createCheckout(any(CheckoutRequest.class)))
                    .thenReturn(new CheckoutResponse("pref_456", "https://init.point"));

            SchoolSubscriptionCheckoutResponse result =
                    schoolSubscriptionService.createCheckout(userId, request);

            // Verifica que o checkout foi criado com preço descontado
            assertThat(result.planPrice()).isEqualByComparingTo(BigDecimal.valueOf(1080.00));
        }

        @Test
        @DisplayName("deve lançar BusinessException quando plano estiver inativo")
        void shouldThrow_whenPlanIsInactive() {
            SchoolPlan inactivePlan = new SchoolPlan(
                    planId, "Plano Inativo", BigDecimal.valueOf(100.00),
                    50, 10, 20, 5, false
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(userService.findById(userId)).thenReturn(user);
            when(schoolPlanService.findById(planId)).thenReturn(inactivePlan);

            assertThatThrownBy(() -> schoolSubscriptionService.createCheckout(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("ativo");

            verify(schoolSubscriptionRepository, never()).save(any());
            verify(paymentGateway, never()).createCheckout(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando escola já tiver assinatura ativa")
        void shouldThrow_whenSchoolAlreadyHasActiveSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(userService.findById(userId)).thenReturn(user);
            when(schoolPlanService.findById(planId)).thenReturn(schoolPlan);
            when(schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE))
                    .thenReturn(true);

            assertThatThrownBy(() -> schoolSubscriptionService.createCheckout(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("ativa");

            verify(schoolSubscriptionRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando escola já tiver assinatura pendente")
        void shouldThrow_whenSchoolAlreadyHasPendingSubscription() {
            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(userService.findById(userId)).thenReturn(user);
            when(schoolPlanService.findById(planId)).thenReturn(schoolPlan);
            when(schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.ACTIVE))
                    .thenReturn(false);
            when(schoolSubscriptionRepository.existsBySchoolIdAndStatus(schoolId, SubscriptionStatus.PENDING_PAYMENT))
                    .thenReturn(true);

            assertThatThrownBy(() -> schoolSubscriptionService.createCheckout(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("pendente");

            verify(schoolSubscriptionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("activeById()")
    final class ActiveById {
        private PaymentResult paymentResult;

        @BeforeEach
        void setUp() {
            paymentResult = new PaymentResult(
                    "visa", "credit_card", 1,
                    "order_123", OffsetDateTime.now()
            );
        }

        @Test
        @DisplayName("deve ativar assinatura e confirmar pagamento com sucesso")
        void shouldActivateSubscription_whenPaymentIsPending() {
            subscription.setStatus(SubscriptionStatus.PENDING_PAYMENT);
            payment.setStatus(PaymentStatus.PENDING);

            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subscription));
            when(schoolPaymentService.findBySchoolSubscriptionId(subscriptionId))
                    .thenReturn(payment);

            String result = schoolSubscriptionService.activeById(subscriptionId, paymentResult);

            assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
            assertThat(payment.getOrderId()).isEqualTo("order_123");
            assertThat(result).isEqualTo(payment.getProviderPaymentId());
        }

        @Test
        @DisplayName("deve retornar null quando pagamento já estiver confirmado (duplicado)")
        void shouldReturnNull_whenPaymentAlreadyPaid() {
            payment.setStatus(PaymentStatus.PAID);

            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subscription));
            when(schoolPaymentService.findBySchoolSubscriptionId(subscriptionId))
                    .thenReturn(payment);

            String result = schoolSubscriptionService.activeById(subscriptionId, paymentResult);

            assertThat(result).isNull();
            assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE); // não muda
        }

        @Test
        @DisplayName("deve mapear credit_card para PaymentMethod.CREDIT_CARD")
        void shouldMapCreditCard_correctly() {
            payment.setStatus(PaymentStatus.PENDING);

            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subscription));
            when(schoolPaymentService.findBySchoolSubscriptionId(subscriptionId))
                    .thenReturn(payment);

            schoolSubscriptionService.activeById(subscriptionId, paymentResult);

            assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        }

        @Test
        @DisplayName("deve mapear pix para PaymentMethod.PIX")
        void shouldMapPix_correctly() {
            payment.setStatus(PaymentStatus.PENDING);
            PaymentResult pixResult = new PaymentResult(
                    "pix", "pix", 1, "order_pix", OffsetDateTime.now()
            );

            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subscription));
            when(schoolPaymentService.findBySchoolSubscriptionId(subscriptionId))
                    .thenReturn(payment);

            schoolSubscriptionService.activeById(subscriptionId, pixResult);

            assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.PIX);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando paymentTypeId não for suportado")
        void shouldThrow_whenPaymentTypeIdIsUnsupported() {
            payment.setStatus(PaymentStatus.PENDING);
            PaymentResult unknownResult = new PaymentResult(
                    "crypto", "crypto_wallet", 1, "order_x", OffsetDateTime.now()
            );

            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subscription));
            when(schoolPaymentService.findBySchoolSubscriptionId(subscriptionId))
                    .thenReturn(payment);

            assertThatThrownBy(() ->
                    schoolSubscriptionService.activeById(subscriptionId, unknownResult))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("nao suportado");
        }
    }

    @Nested
    @DisplayName("cancelById()")
    final class CancelById {
        @Test
        @DisplayName("deve cancelar assinatura ativa sem alterar o pagamento")
        void shouldCancelActiveSubscription_withoutChangingPayment() {
            subscription.setStatus(SubscriptionStatus.ACTIVE);

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subscription));

            schoolSubscriptionService.cancelById(userId, subscriptionId);

            assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELED);
            verifyNoInteractions(schoolPaymentService);
        }

        @Test
        @DisplayName("deve cancelar assinatura pendente e marcar pagamento como FAILED")
        void shouldCancelPendingSubscription_andMarkPaymentAsFailed() {
            subscription.setStatus(SubscriptionStatus.PENDING_PAYMENT);
            payment.setStatus(PaymentStatus.PENDING);

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subscription));
            when(schoolPaymentService.findBySchoolSubscriptionId(subscriptionId))
                    .thenReturn(payment);

            schoolSubscriptionService.cancelById(userId, subscriptionId);

            assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.CANCELED);
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        }

        @Test
        @DisplayName("deve lançar AccessDeniedException quando assinatura pertencer a outra escola")
        void shouldThrowAccessDenied_whenSubscriptionBelongsToDifferentSchool() {
            SchoolSubscription subDeOutraEscola = new SchoolSubscription(
                    subscriptionId, outraEscola, schoolPlan, 12, "Plano Básico",
                    BigDecimal.valueOf(1200.00), 50, 10, 20, 5,
                    LocalDate.now(), LocalDate.now().plusMonths(12),
                    SubscriptionStatus.ACTIVE
            );

            when(schoolService.findByUserId(userId)).thenReturn(school);
            when(schoolSubscriptionRepository.findById(subscriptionId))
                    .thenReturn(Optional.of(subDeOutraEscola));

            assertThatThrownBy(() ->
                    schoolSubscriptionService.cancelById(userId, subscriptionId))
                    .isInstanceOf(AccessDeniedException.class);

            assertThat(subDeOutraEscola.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        }
    }
}
