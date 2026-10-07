import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { Observable } from 'rxjs';
import { SchoolResponseDto } from '../dto/school-response.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { UpdateSchoolRequestDto } from '../dto/update-school.request.dto';

@Injectable({
  providedIn: 'root',
})
export class SchoolApi {
  private cache = new Map<string, any>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) {
    this.cacheReset.register(() => this.cache.clear());
  }

  mySchool(): Observable<SchoolResponseDto> {
    const key: string = `my-school`;
    if (!this.cache.has(key)) {
      const request$ = this.apiService.get<SchoolResponseDto>(
        `${ApiConfig.endpoints.school.base}/my`
      ) as unknown as Observable<SchoolResponseDto>;
      this.cache.set(key, request$);
    }
    return this.cache.get(key)!;
  }

  update(id: string, dto: UpdateSchoolRequestDto): Observable<any> {
    this.cache.clear();
    return this.apiService.put<any>(
      `${ApiConfig.endpoints.school.base}/${id}`,
      dto
    ) as unknown as Observable<any>;
  }
}
