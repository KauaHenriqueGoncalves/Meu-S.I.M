import { Injectable } from '@angular/core';
import { ApiService } from '../../../../core/services/api/api.service';
import { CacheResetService } from '../../../../core/services/cache-reset/cache-reset.service';
import { Observable, shareReplay } from 'rxjs';
import { BillingDiscountResponseDto } from '../dto/billing-discount-response.dto';
import { ApiConfig } from '../../../../core/config/api.config';
import { BillingDiscountRequestDto } from '../dto/billing-discount-request.dto';

@Injectable({
  providedIn: 'root',
})
export class AdminBillingdiscountApi {
  private $request!: Observable<BillingDiscountResponseDto> | null;

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) {
    this.cacheReset.register(() => this.clearCache());
  }

  findAll(): Observable<BillingDiscountResponseDto> {
    if (!this.$request) {
      this.$request = this.apiService.get<BillingDiscountResponseDto>(
        ApiConfig.endpoints.billingdiscount.base
      ).pipe(shareReplay(1)) as unknown as Observable<BillingDiscountResponseDto>;
    }
    return this.$request;
  }

  create(data: BillingDiscountRequestDto): Observable<any> {
    this.clearCache();
    return this.apiService.post<BillingDiscountRequestDto>(
      ApiConfig.endpoints.billingdiscount.base,
      data
    ) as Observable<any>;
  }

  update(id: string, data: BillingDiscountRequestDto): Observable<any> {
    this.clearCache();
    return this.apiService.put<BillingDiscountRequestDto>(
      `${ApiConfig.endpoints.billingdiscount.base}/${id}`,
      data
    ) as Observable<any>;
  }

  deleteById(id: string): Observable<any> {
    this.clearCache();
    return this.apiService.delete(
      `${ApiConfig.endpoints.billingdiscount.base}/${id}`
    ) as Observable<any>;
  }

  clearCache(): void {
    this.$request = null;
  }
}
