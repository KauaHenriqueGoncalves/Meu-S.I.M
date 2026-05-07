import { Injectable } from '@angular/core';
import { catchError, EMPTY, timeout } from 'rxjs';
import { SubscriptionApi } from '../../api/subscription.api';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { SubscriptionCheckResponse } from '../../dto/subscription-check-response.dto';
import { AuthService } from '../../../../core/auth/service/auth.service';

@Injectable({
  providedIn: 'root',
})
export class SubscriptionCheckService {
  subscription: SubscriptionCheckResponse | undefined;

  constructor(
    private authService: AuthService,
    private subscriptionApi: SubscriptionApi,
    private notificationService: NotificationService
  ) { }

  getCheck(): boolean {
    return this.subscription ? true : false;
  }

  check(): void {
    setTimeout(() => {
      if (this.authService.getPayload()?.scope != 'school_admin') return;

      this.subscriptionApi.findActive()
        .pipe(
          timeout(10000),
          catchError(() => {
            this.notificationService.notify({
              type: 'warning',
              text: 'Você não possui uma licença ativa.'
            });
            return EMPTY;
          })
        )
        .subscribe({
          next: (res: SubscriptionCheckResponse) => {
            this.subscription = res;
          }
        });
    }, 1000);
  }
}
