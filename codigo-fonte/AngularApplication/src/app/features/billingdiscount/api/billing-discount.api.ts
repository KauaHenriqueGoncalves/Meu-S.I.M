import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { Observable, shareReplay } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { BillingDiscountClientResponse } from '../dto/billing-discount-client-response.dto';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';

@Injectable({
  providedIn: 'root',
})
export class BillingDiscountApi {
  private discount$: Observable<BillingDiscountClientResponse[]> | undefined;

  constructor(
    private apiService: ApiService,
    private cacheResetService: CacheResetService 
  ) { 
    this.cacheResetService.register(() => this.refreshDiscount());
  }

  findAllToClient(): Observable<BillingDiscountClientResponse[]> {
    if (!this.discount$) {
      this.discount$ = this.apiService.get<BillingDiscountClientResponse[]>(
        ApiConfig.endpoints.billingdiscount.toClient,
      ).pipe(shareReplay(1)) as unknown as Observable<BillingDiscountClientResponse[]>;
    }
    
    return this.discount$;
  }

  refreshDiscount(): void {
    this.discount$ = undefined;
  }
}
