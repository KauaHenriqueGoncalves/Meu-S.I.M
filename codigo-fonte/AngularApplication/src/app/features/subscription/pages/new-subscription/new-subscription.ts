import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SchoolPlanApi } from '../../../schoolplan/api/school-plan.api';
import { SchoolPlanClientResponseDto } from '../../../schoolplan/dto/school-plan-client-response.dto';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { DecimalPipe } from '@angular/common';
import { BillingDiscountApi } from '../../../billingdiscount/api/billing-discount.api';
import { BillingDiscountClientResponse } from '../../../billingdiscount/dto/billing-discount-client-response.dto';
import { Decimal } from 'decimal.js';
import { SubscriptionCheckoutResponseDto } from '../../dto/subscription-checkout-response.dto';
import { SubscriptionRequestDto } from '../../dto/subscription-request.dto';
import { SubscriptionApi } from '../../api/subscription.api';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';

@Component({
  selector: 'app-new-subscription',
  imports: [DecimalPipe, SpinnerToButton],
  templateUrl: './new-subscription.html',
  styleUrl: './new-subscription.sass',
})
export class NewSubscription implements OnInit {
  plans: SchoolPlanClientResponseDto[] = [];
  discounts: BillingDiscountClientResponse[] = [];

  constructor(
    private schoolPlanApi: SchoolPlanApi,
    private billingDiscountApi: BillingDiscountApi,
    private subscriptionApi: SubscriptionApi,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.requestSchoolPlan();
    this.requestDiscounts();
  }

  incrementMonths(plan: SchoolPlanClientResponseDto) {
    if (plan.selectedMonths < 12) {
      plan.selectedMonths++;
      const total = plan.basePrice.mul(plan.selectedMonths);
      const discount = this.getBestDiscount(plan.selectedMonths);
      const finalPrice = total.mul(
        new Decimal(1).minus(discount.div(100))
      );
      plan.monthlyPrice = finalPrice;
    }
    this.cdr.detectChanges();
  }

  decrementMonths(plan: SchoolPlanClientResponseDto) {
    if (plan.selectedMonths > 1) {
      plan.selectedMonths--;
      const total = plan.basePrice.mul(plan.selectedMonths);
      const discount = this.getBestDiscount(plan.selectedMonths);
      const finalPrice = total.mul(
        new Decimal(1).minus(discount.div(100))
      );
      plan.monthlyPrice = finalPrice;
    }
    this.cdr.detectChanges();
  }

  getBestDiscount(months: number): Decimal {
    let bestDiscount = new Decimal(0);
    let bestMonths = -1;

    for (const d of this.discounts) {
      if (d.months <= months && d.months > bestMonths) {
        bestMonths = d.months;
        bestDiscount = d.discountPercent;
      }
    }

    return bestDiscount;
  }

  paySubscription(plan: SchoolPlanClientResponseDto): void {
    const subscription: SubscriptionRequestDto = {
      schoolPlanId: plan.id,
      months: plan.selectedMonths
    }
    this.requestPaySubscription(subscription, plan);
  }

  private requestSchoolPlan(): void {
    this.schoolPlanApi.findAllToClient()
      .pipe(
        timeout(10000),
        catchError((error) => {
          return throwError(() => error);
        })
      )
      .subscribe({
        next: (res: SchoolPlanClientResponseDto[]) => {
          this.plans = res.map(plan =>
          ({
            ...plan,
            basePrice: new Decimal(plan.monthlyPrice),
            monthlyPrice: new Decimal(plan.monthlyPrice),
            selectedMonths: 1
          }));
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro inesperado, tente novamente mais tarde'
          });
        }
      });
  }

  private requestDiscounts(): void {
    this.billingDiscountApi.findAllToClient()
      .pipe(
        timeout(10000),
        catchError((error) => {
          return throwError(() => error);
        })
      )
      .subscribe({
        next: (res: BillingDiscountClientResponse[]) => {
          this.discounts = res.map(discount =>
          ({
            ...discount,
            discountPercent: new Decimal(discount.discountPercent)
          }));
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro inesperado, tente novamente mais tarde'
          });
        }
      });
  }

  private requestPaySubscription(subscription: SubscriptionRequestDto, plan: SchoolPlanClientResponseDto): void {
    if (plan.isLoading) return;

    plan.isLoading = true;

    this.subscriptionApi.paySubscription(subscription)
      .pipe(
        timeout(10000),
        catchError((error) => {
          return throwError(() => error);
        }),
        finalize(() => {
          plan.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: SubscriptionCheckoutResponseDto) => {
          window.open(res.initPoint, '_blank');
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro inesperado, tente novamente mais tarde'
          });
        }
      });
  }
}
