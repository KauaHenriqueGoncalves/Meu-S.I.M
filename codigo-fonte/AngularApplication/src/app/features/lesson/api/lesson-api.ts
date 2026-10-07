import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { PageResponse } from '../../../shared/models/page-response.model';
import { LessonSimpleViewResponseDTO } from '../dto/lesson-simples-view-response.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { Observable, shareReplay } from 'rxjs';
import { AgendaDayResponseDto } from '../dto/agenda-day-response.dto';
import { LessonDetailViewResponseDto } from '../dto/lesson-detail-view-response.dto';
import { GetToCreateLessonRequestDto } from '../dto/get-to-create-lesson-request.dto';
import { LabelToCreateLessonResponseDto } from '../dto/label-to-create-lesson-response.dto';
import { CreateLessonRequestDto } from '../dto/create-lesson-request.dto';

@Injectable({
  providedIn: 'root',
})
export class LessonApi {
  private cache = new Map<string, any>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) { 
    this.cacheReset.register(() => this.cache.clear());
  }

  page(classroomId: string, page: number, size: number): Observable<PageResponse<LessonSimpleViewResponseDTO>> {
    const key = `${classroomId}-${page}-${size}-page`;
    if (!this.cache.has(key)) {
      const request$ = this.apiService.get<PageResponse<LessonSimpleViewResponseDTO>>(
        `${ApiConfig.endpoints.lesson.base}/${classroomId}?page=${page}&size=${size}`
      ).pipe(shareReplay(1)) as unknown as Observable<PageResponse<LessonSimpleViewResponseDTO>>;
      this.cache.set(key, request$);
    }
    return this.cache.get(key)!;
  }

  getDetails(classroomId: string,lessonId: string) {
    const key = `${classroomId}-${lessonId}-details`;
    if (!this.cache.has(key)) {
      const request$ = this.apiService.get<LessonDetailViewResponseDto>(
        `${ApiConfig.endpoints.lesson.base}/${classroomId}/${lessonId}`
      ).pipe(shareReplay(1)) as unknown as Observable<LessonDetailViewResponseDto>;
      this.cache.set(key, request$);
    }
    return this.cache.get(key)!;
  }

  getAgendaDays(classroomId: string, year: number, month: number): Observable<AgendaDayResponseDto[]> {
    const key = `${classroomId}-${year}-${month}-agendaDays`;
    if (!this.cache.has(key)) {
      const request$ = this.apiService.get<AgendaDayResponseDto[]>(
        `${ApiConfig.endpoints.lesson.base}/${classroomId}/agenda?year=${year}&month=${month}`
      ).pipe(shareReplay(1)) as unknown as Observable<AgendaDayResponseDto[]>;
      this.cache.set(key, request$);
    }
    return this.cache.get(key)!;
  }

  getLabel(classroomId: string, label: GetToCreateLessonRequestDto) {
    const key = `${classroomId}-${label.lessonDate}-${label.scheduleId}-label`;
    if (!this.cache.has(key)) {
      const request$ = this.apiService.get<LabelToCreateLessonResponseDto>(
        `${ApiConfig.endpoints.lesson.base}/${classroomId}/agenda/label`,
        label
      ).pipe(shareReplay(1)) as unknown as Observable<LabelToCreateLessonResponseDto>;
      this.cache.set(key, request$);
    }
    return this.cache.get(key)!;
  }

  create(body: CreateLessonRequestDto): Observable<any> {
    this.cache.clear();
    return this.apiService.post(
      `${ApiConfig.endpoints.lesson.base}`,
      body
    );
  }
}
