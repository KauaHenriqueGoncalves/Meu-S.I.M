package com.system.application.core.schoolpayment;

import com.system.application.core.schoolpayment.dto.SchoolPaymentRequest;
import com.system.application.core.schoolpayment.enums.PaymentMethod;
import com.system.application.core.schoolpayment.enums.PaymentStatus;
import com.system.application.core.schoolsubscription.SchoolSubscription;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "school_payment")
public class SchoolPayment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_subscription_id", nullable = false)
    private SchoolSubscription schoolSubscription;

    // TODO: Implements cupom

    @Column(name = "discount_amount", precision = 6, scale = 2, nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "original_amount", precision = 6, scale = 2, nullable = false)
    private BigDecimal originalAmount;

    @Column(name = "amount", precision = 6, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "installments")
    private Integer installments;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "order_id")
    private String orderId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "provider_payment_id")
    private String providerPaymentId;

    public SchoolPayment() {
    }

    public SchoolPayment(
            UUID id,
            SchoolSubscription schoolSubscription,
            BigDecimal discountAmount,
            BigDecimal originalAmount,
            BigDecimal amount,
            PaymentMethod paymentMethod,
            Integer installments,
            String paymentType,
            String orderId,
            Instant paidAt,
            PaymentStatus status,
            String providerPaymentId
    ) {
        this.id = id;
        this.schoolSubscription = schoolSubscription;
        this.discountAmount = discountAmount;
        this.originalAmount = originalAmount;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.installments = installments;
        this.paymentType = paymentType;
        this.orderId = orderId;
        this.paidAt = paidAt;
        this.status = status;
        this.providerPaymentId = providerPaymentId;
    }

    public static SchoolPayment createInit(SchoolPaymentRequest request) {
        return new SchoolPayment(
                null,
                request.schoolSubscription(),
                request.discountAmount(),
                request.originalAmount(),
                request.amount(),
                null,
                null,
                null,
                null,
                null,
                request.status(),
                request.providerPaymentId()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SchoolSubscription getSchoolSubscription() {
        return schoolSubscription;
    }

    public void setSchoolSubscription(SchoolSubscription schoolSubscription) {
        this.schoolSubscription = schoolSubscription;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public String getProviderPaymentId() {
        return providerPaymentId;
    }

    public void setProviderPaymentId(String providerPaymentId) {
        this.providerPaymentId = providerPaymentId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SchoolPayment that = (SchoolPayment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
