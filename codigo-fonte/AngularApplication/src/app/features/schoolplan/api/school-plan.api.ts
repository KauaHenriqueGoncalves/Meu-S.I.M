import { Injectable } from '@angular/core';
import { ApiConfig } from '../../../core/config/api.config';
import { ApiService } from '../../../core/services/api/api.service';
import { Observable, shareReplay } from 'rxjs';
import { SchoolPlanClientResponseDto } from '../dto/school-plan-client-response.dto';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';

@Injectable({
  providedIn: 'root',
})
export class SchoolPlanApi {
  private plans$: Observable<SchoolPlanClientResponseDto[]> | undefined;
  
  constructor(
    private apiService: ApiService,
    private cacheResetService: CacheResetService
  ) { 
    this.cacheResetService.register(() => this.refreshPlans());
  }

  findAllToClient(): Observable<SchoolPlanClientResponseDto[]> {
    if (!this.plans$) {
      this.plans$ = this.apiService
        .get<SchoolPlanClientResponseDto[]>(
          ApiConfig.endpoints.schoolPlan.toClient,
        ).pipe(shareReplay(1)) as unknown as Observable<SchoolPlanClientResponseDto[]>;
    }
    
    return this.plans$;
  }

  refreshPlans(): void {
    this.plans$ = undefined;
  }
}
