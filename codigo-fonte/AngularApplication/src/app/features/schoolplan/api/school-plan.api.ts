import { Injectable } from '@angular/core';
import { ApiConfig } from '../../../core/config/api.config';
import { ApiService } from '../../../core/services/api/api.service';
import { Observable, shareReplay } from 'rxjs';
import { SchoolPlanClientResponseDto } from '../dto/school-plan-client-response.dto';

@Injectable({
  providedIn: 'root',
})
export class SchoolPlanApi {
  private plans$: Observable<any> | undefined;
  
  constructor(
    private apiService: ApiService
  ) { }

  findAllToClient(): Observable<SchoolPlanClientResponseDto[]> {
    if (!this.plans$) {
      this.plans$ = this.apiService
        .get<SchoolPlanClientResponseDto[]>(
          ApiConfig.endpoints.schoolPlan.toClient,
        )
        .pipe(shareReplay(1));
    }
    return this.plans$;
  }

  refreshPlans(): void {
    this.plans$ = undefined;
  }
}
