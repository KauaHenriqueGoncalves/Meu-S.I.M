import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { ScheduleRequestDto } from '../dto/schedule-request.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { ScheduleResponseDto } from '../dto/schedule-response.dto';
import { Observable, shareReplay } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ScheduleApi {
  private cache = new Map<string, Observable<ScheduleResponseDto[]>>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) {
    this.cacheReset.register(() => this.cleanCache());
  }

  findAll(classroomId: string): Observable<ScheduleResponseDto[]> {
    const key: string = `${classroomId}`;

    if (!this.cache.has(key)) {
      const endpoint: string = ApiConfig.endpoints.schedule.base.replace('{classroomId}', classroomId);
      const $request = this.apiService.get<ScheduleResponseDto[]>(endpoint)
        .pipe(shareReplay(1)) as unknown as Observable<ScheduleResponseDto[]>;
      this.cache.set(key, $request);
    }
    return this.cache.get(key)!;
  }

  create(classroomId: string, data: ScheduleRequestDto): Observable<any> {
    const endpoint: string = ApiConfig.endpoints.schedule.base.replace('{classroomId}', classroomId);
    this.cleanCache();
    return this.apiService.post<ScheduleRequestDto>(
      endpoint,
      data
    ) as Observable<any>;
  }

  update(classroomId: string, scheduleId: string, data: ScheduleRequestDto) {
    const endpoint: string = ApiConfig.endpoints.schedule.base.replace('{classroomId}', classroomId);
    this.cleanCache();
    return this.apiService.put<ScheduleRequestDto>(
      `${endpoint}/${scheduleId}`,
      data
    ) as Observable<any>;
  }

  delete(classroomId: string, scheduleId: string) {
    const endpoint: string = ApiConfig.endpoints.schedule.base.replace('{classroomId}', classroomId);
    this.cleanCache();
    return this.apiService.delete(
      `${endpoint}/${scheduleId}`
    ) as Observable<any>;
  }

  cleanCache(): void {
    this.cache.clear();
  }
}
