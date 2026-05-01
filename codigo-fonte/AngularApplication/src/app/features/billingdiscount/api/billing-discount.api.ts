import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { Observable, shareReplay } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { BillingDiscountClientResponse } from '../dto/billing-discount-client-response.dto';

@Injectable({
  providedIn: 'root',
})
export class BillingDiscountApi {
  private discount$: Observable<any> | undefined;

  constructor(
    private apiService: ApiService
  ) { }

  findAllToClient(): Observable<BillingDiscountClientResponse[]> {
    if (!this.discount$) {
      this.discount$ = this.apiService.get<BillingDiscountClientResponse[]>(
        ApiConfig.endpoints.billingdiscount.toClient,
      )
      .pipe(shareReplay(1));
    }

    return this.discount$;
  }

  refreshDiscount(): void {
    this.discount$ = undefined;
  }
}
