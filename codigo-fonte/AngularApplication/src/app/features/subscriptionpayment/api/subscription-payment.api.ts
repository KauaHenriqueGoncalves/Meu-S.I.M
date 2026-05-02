import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { ApiConfig } from '../../../core/config/api.config';
import { Observable, shareReplay } from 'rxjs';
import { SchoolSubscriptionDetailResponse } from '../../subscription/dto/subscription-detail-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SubscriptionPaymentApi {
  private cache = new Map<string, Observable<SchoolSubscriptionDetailResponse>>();

  constructor(
    private api: ApiService
  ) { }

  findById(id: string): Observable<SchoolSubscriptionDetailResponse> {
    const key: string = id;

    if (!this.cache.has(key)) {
      const request$ = this.api.get(
        `${ApiConfig.endpoints.subscription.base}/${id}`
      ).pipe(shareReplay(1)) as unknown as Observable<SchoolSubscriptionDetailResponse>;
      this.cache.set(key, request$);
    }

    return this.cache.get(key)!;
  }

  refreshCache(): void {
    this.cache.clear();
  }
}
