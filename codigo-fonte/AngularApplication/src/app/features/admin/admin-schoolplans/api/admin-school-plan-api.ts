import { Injectable } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { SchoolPlanResponseDto } from '../dto/school-plan-response.dto';
import { CacheResetService } from '../../../../core/services/cache-reset/cache-reset.service';
import { ApiService } from '../../../../core/services/api/api.service';
import { ApiConfig } from '../../../../core/config/api.config';
import { CreateSchoolPlanRequestDto } from '../dto/create-school-plan-request.dto';
import { UpdateSchoolPlanRequestDto } from '../dto/update-school-plan-request.dto';

@Injectable({
  providedIn: 'root',
})
export class AdminSchoolPlanApi {
  private $request!: Observable<SchoolPlanResponseDto> | null;

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) {
    this.cacheReset.register(() => this.clearCache());
  }

  findAll(): Observable<SchoolPlanResponseDto> {
    if (!this.$request) {
      this.$request = this.apiService.get<SchoolPlanResponseDto>(
        ApiConfig.endpoints.schoolPlan.base
      ).pipe(shareReplay(1)) as unknown as Observable<SchoolPlanResponseDto>;
    }
    return this.$request;
  }

  create(data: CreateSchoolPlanRequestDto): Observable<any> {
    this.clearCache();
    return this.apiService.post<CreateSchoolPlanRequestDto>(
      ApiConfig.endpoints.schoolPlan.base,
      data
    ) as Observable<any>;
  }

  update(id: string, data: UpdateSchoolPlanRequestDto): Observable<any> {
    this.clearCache();
    return this.apiService.put<UpdateSchoolPlanRequestDto>(
      `${ApiConfig.endpoints.schoolPlan.base}/${id}`,
      data
    ) as Observable<any>;
  }

  deleteById(id: string): Observable<any> {
    this.clearCache();
    return this.apiService.delete(
      `${ApiConfig.endpoints.schoolPlan.base}/${id}`
    ) as Observable<any>;
  }

  clearCache(): void {
    this.$request = null;
  }
}
