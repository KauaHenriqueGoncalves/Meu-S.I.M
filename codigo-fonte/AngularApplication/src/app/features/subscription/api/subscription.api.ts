import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { ApiConfig } from '../../../core/config/api.config';
import { SubscriptionRequestDto } from '../dto/subscription-request.dto';
import { SubscriptionCheckoutResponseDto } from '../dto/subscription-checkout-response.dto';
import { Observable, shareReplay } from 'rxjs';
import { PageResponse } from '../../../shared/models/page-response.model';
import { SubscriptionResponseDto } from '../dto/subscription-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SubscriptionApi {
  private cache = new Map<string, Observable<PageResponse<SubscriptionResponseDto>>>();

  constructor(
    private apiService: ApiService
  ) { }

  findAll(page: number, size: number): Observable<PageResponse<SubscriptionResponseDto>> {
    const key: string = `${page}-${size}`;

    if (!this.cache.has(key)) {
      const request$ = this.apiService
        .get<PageResponse<SubscriptionResponseDto>>(
          `${ApiConfig.endpoints.subscription.base}?page=${page}&size=${size}`
        ).pipe(shareReplay(1)) as unknown as Observable<PageResponse<SubscriptionResponseDto>>;
      this.cache.set(key, request$);
    }

    return this.cache.get(key)!;
  }

  paySubscription(subscription: SubscriptionRequestDto): Observable<SubscriptionCheckoutResponseDto> {
    this.refreshAllSubscriptions();

    return this.apiService.post<SubscriptionCheckoutResponseDto>(
      ApiConfig.endpoints.subscription.base, 
      subscription
    ) as unknown as Observable<SubscriptionCheckoutResponseDto>;
  }

  refreshAllSubscriptions(): void {
    this.cache.clear();
  }
}
