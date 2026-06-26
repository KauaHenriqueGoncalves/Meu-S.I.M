import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { LegalGuardianViewSimpleResponseDto } from '../dto/legal-guardian-view-simple-response.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { Observable, shareReplay } from 'rxjs';
import { LegalGuardianDetailResponseDto } from '../dto/legal-guardian-detail-response.dto';
import { CreateLegalGuardianRequestDto } from '../dto/create-legal-guardian-request.dto';
import { UpdateLegalGuardianRequestDto } from '../dto/update-legal-guardian-request.dto';
import { UserChangePasswordRequestDto } from '../../user/dto/user-change-password-request.dto';

@Injectable({
  providedIn: 'root',
})
export class LegalGuardianApi {
  private cacheViewSimple = new Map<string, Observable<LegalGuardianViewSimpleResponseDto[]>>();
  private cacheDetail = new Map<string, Observable<LegalGuardianDetailResponseDto>>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) {
    this.cacheReset.register(() => this.refreshAllCaches());
  }

  findAll(name: string, page: number, size: number): Observable<LegalGuardianViewSimpleResponseDto[]> {
    const key: string = `${name}-${page}-${size}`;

    if (!this.cacheViewSimple.has(key)) {
      const request$ = this.apiService.get<LegalGuardianViewSimpleResponseDto[]>(
        `${ApiConfig.endpoints.legalGuardian.base}?name=${name}&page=${page}&size=${size}`
      ).pipe(shareReplay(1)) as unknown as Observable<LegalGuardianViewSimpleResponseDto[]>
      this.cacheViewSimple.set(key, request$);
    }

    return this.cacheViewSimple.get(key)!;
  }

  findById(id: string): Observable<LegalGuardianDetailResponseDto> {
    const key: string = `${id}-detail`;

    if (!this.cacheDetail.has(key)) {
      const request$ = this.apiService.get<LegalGuardianDetailResponseDto>(
        `${ApiConfig.endpoints.legalGuardian.base}/${id}`
      ).pipe(shareReplay(1)) as unknown as Observable<LegalGuardianDetailResponseDto>;
      this.cacheDetail.set(key, request$);
    }

    return this.cacheDetail.get(key)!;
  }

  create(data: CreateLegalGuardianRequestDto): Observable<any> {
    this.cacheViewSimple.clear();
    return this.apiService.post(
      ApiConfig.endpoints.legalGuardian.base,
      data
    ) as Observable<any>;
  }

  update(id: string, data: UpdateLegalGuardianRequestDto): Observable<any> {
    this.refreshAllCaches();
    return this.apiService.put(
      `${ApiConfig.endpoints.legalGuardian.base}/${id}`,
      data
    ) as Observable<any>;
  }

  changePassword(id: string, data: UserChangePasswordRequestDto): Observable<any> {
    const endpoint: string = ApiConfig.endpoints.legalGuardian.changePassword.replace('{id}', id);
    this.cacheDetail.clear();
    return this.apiService.patch(
      endpoint,
      data
    ) as Observable<any>;  
  }

  deleteById(id: string) {
    this.cacheViewSimple.clear();
    return this.apiService.delete(
      `${ApiConfig.endpoints.legalGuardian.base}/${id}`
    ) as Observable<any>;
  }

  refreshAllCaches(): void {
    this.cacheViewSimple.clear();
    this.cacheDetail.clear();
  }
}
