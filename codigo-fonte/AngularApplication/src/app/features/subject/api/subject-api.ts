import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { SubjectResponseDto } from '../dto/subject-response.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { Observable, shareReplay } from 'rxjs';
import { SubjectRequestDto } from '../dto/subject-request.dto';
import { PageResponse } from '../../../shared/models/page-response.model';

@Injectable({
  providedIn: 'root',
})
export class SubjectApi {
  private cache = new Map<string, Observable<PageResponse<SubjectResponseDto>>>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) {
    this.cacheReset.register(() => this.refreshCache());
  }

  findAll(page: number, size: number): Observable<PageResponse<SubjectResponseDto>> {
    const key: string = `${page}-${size}`;

    if (!this.cache.has(key)) {
      const request$ = this.apiService.get<SubjectResponseDto>(
        `${ApiConfig.endpoints.subject.base}?page=${page}&size=${size}`
      ).pipe(shareReplay(1)) as unknown as Observable<PageResponse<SubjectResponseDto>>;
      this.cache.set(key, request$);
    }

    return this.cache.get(key)!;
  }

  create(data: SubjectRequestDto): Observable<any> {
    this.cache.clear();
    return this.apiService.post<SubjectRequestDto>(
      ApiConfig.endpoints.subject.base,
      data
    ) as Observable<any>;
  }

  update(id: string, data: SubjectRequestDto): Observable<any> {
    this.cache.clear();
    return this.apiService.put<SubjectRequestDto>(
      `${ApiConfig.endpoints.subject.base}/${id}`,
      data
    ) as Observable<any>;
  }

  deleteById(id: string): Observable<any> {
    this.cache.clear();
    return this.apiService.delete(
      `${ApiConfig.endpoints.subject.base}/${id}`
    ) as Observable<any>;
  }

  refreshCache(): void {
    this.cache.clear();
  }
}
