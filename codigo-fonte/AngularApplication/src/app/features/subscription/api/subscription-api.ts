import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { ApiConfig } from '../../../core/config/api.config';
import { SubscriptionRequestDto } from '../dto/subscription-request.dto';
import { SubscriptionCheckoutResponseDto } from '../dto/subscription-checkout-response.dto';
import { Observable } from 'rxjs';
import { HttpEvent } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class SubscriptionApi {
  constructor(
    private apiService: ApiService
  ) { }

  paySubscription(subscription: SubscriptionRequestDto): Observable<SubscriptionCheckoutResponseDto> {
    return this.apiService.post<SubscriptionCheckoutResponseDto>(
      ApiConfig.endpoints.subscription.base, 
      subscription
    ) as unknown as Observable<SubscriptionCheckoutResponseDto>;
  }
}
