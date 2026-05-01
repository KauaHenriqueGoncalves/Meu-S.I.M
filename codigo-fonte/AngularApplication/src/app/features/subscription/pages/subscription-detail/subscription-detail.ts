import { DatePipe, DecimalPipe, NgClass } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { SubscriptionStatus } from '../../enum/subscription-status.enum';
import { SchoolSubscriptionDetailResponse } from '../../dto/subscription-detail-response.dto';
import { Router } from '@angular/router';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { FormsModule } from '@angular/forms';
import { WarningSvg } from '../../../../shared/components/svg/warning.svg';
import { PaymentStatus } from '../../../subscriptionpayment/enum/payment-status.enum';
import { SubscriptionPaymentApi } from '../../../subscriptionpayment/api/subscription-payment.api';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { catchError, finalize, throwError, timeout } from 'rxjs';

@Component({
  selector: 'app-subscription-detail',
  imports: [DatePipe, DecimalPipe, NgClass, SpinnerToButton, ArrowLeftSvg, FormsModule, WarningSvg],
  templateUrl: './subscription-detail.html',
  styleUrl: './subscription-detail.sass',
})
export class SubscriptionDetail implements OnInit {
  Status = SubscriptionStatus;
  PayStatus = PaymentStatus;
  detail?: SchoolSubscriptionDetailResponse;
  isLoadingPage = true;

  isCanceling = false;
  isPaying = false;

  showCancelModal = false;
  cancelConfirmationText = '';
  readonly requiredCancelText = 'eu quero cancelar a licença';

  constructor(
    private paymentApi: SubscriptionPaymentApi,
    private notificationService: NotificationService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    const id = history.state.id;
    if (id) {
      this.loadDetails(id);
    } 
    else {
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, tente novamente mais tarde'
      });
    }
  }

  loadDetails(id: string): void {
    this.paymentApi.findById(id)
      .pipe(
        timeout(10000),
        catchError((error) => {
          return throwError(() => error);
        }),
        finalize(() => {
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: SchoolSubscriptionDetailResponse) => {
          this.detail = res;
          this.isLoadingPage = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro inesperado, tente novamente mais tarde'
          });
        }
      })
  }

  goBack(): void {
    this.router.navigate(['/app/my-subscriptions']);
  }

  openCancelModal(): void {
    this.showCancelModal = true;
    this.cancelConfirmationText = '';
  }

  closeCancelModal(): void {
    if (this.isCanceling) return;
    this.showCancelModal = false;
    this.cancelConfirmationText = '';
  }

  confirmCancel(): void {
    if (this.cancelConfirmationText !== this.requiredCancelText || this.isCanceling || !this.detail) return;

    this.isCanceling = true;

    // Simulação da sua API de cancelamento
    console.log('Enviando requisição de cancelamento para:', this.detail.id);

    // Retorno
    // this.isCanceling = false; 
    //   this.showCancelModal = false;
    //   // Atualiza o status localmente para refletir na tela
    //   if (this.detail) {
    //     this.detail.subscriptionStatus = SubscriptionStatus.CANCELED;
    //   }
    //   this.cdr.detectChanges();
  }

  paySubscription(): void {
    if (this.isPaying || !this.detail) return;
    this.isPaying = true;

    // Aqui vai a chamada para gerar o link do MercadoPago/Asaas
    console.log('Redirecionando para pagamento do pedido:', this.detail.orderId);
    setTimeout(() => { this.isPaying = false; }, 2000);
  }

  canCancel(): boolean {
    if (!this.detail) return false;
    return this.detail.subscriptionStatus !== SubscriptionStatus.CANCELED &&
      this.detail.subscriptionStatus !== SubscriptionStatus.EXPIRED;
  }
}
