import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { CreateStudentRequestDto } from '../dto/create-student-request.dto';
import { UpdateStudentRequestDto } from '../dto/update-student-request.dto';
import { Observable, shareReplay } from 'rxjs';
import { PageResponse } from '../../../shared/models/page-response.model';
import { StudentResponseDto } from '../dto/student-response.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { StudentDetailRequestDto } from '../dto/student-detail-request.dto';

@Injectable({
  providedIn: 'root',
})
export class StudentApi {
  private cachePage = new Map<string, Observable<PageResponse<StudentResponseDto>>>();
  private cacheDetail = new Map<string, Observable<StudentDetailRequestDto>>();
  private cacheLegalGuardian = new Map<string, Observable<StudentResponseDto[]>>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) {
    this.cacheReset.register(() => this.refreshCaches());
  }

  findAll(name: string, page: number, size: number): Observable<PageResponse<StudentResponseDto>> {
    const key: string = `${name}-${page}-${size}`;

    if (!this.cachePage.has(key)) {
      const request$ = this.apiService.get<PageResponse<StudentResponseDto>>(
        `${ApiConfig.endpoints.student.base}?name=${name}&page=${page}&size=${size}`
      ).pipe(shareReplay(1)) as unknown as Observable<PageResponse<StudentResponseDto>>;
      this.cachePage.set(key, request$);
    }

    return this.cachePage.get(key)!;
  }

  findById(id: string): Observable<StudentDetailRequestDto> {
    const key = `${id}`;

    if (!this.cacheDetail.has(key)) {
      const request$ = this.apiService.get<StudentDetailRequestDto>(
        `${ApiConfig.endpoints.student.base}/${id}`
      ).pipe(shareReplay(1)) as unknown as Observable<StudentDetailRequestDto>;
      this.cacheDetail.set(key, request$);
    }

    return this.cacheDetail.get(key)!;
  }

  findByLegalGuardian(legalGuardianId: string): Observable<StudentResponseDto[]> {
    const key: string = `${legalGuardianId}-legal-guardian`;

    if (!this.cacheLegalGuardian.has(key)) {
      const request$ = this.apiService.get<StudentResponseDto[]>(
        `${ApiConfig.endpoints.student.legalGuardian}/${legalGuardianId}`
      ).pipe(shareReplay(1)) as unknown as Observable<StudentResponseDto[]>;
      this.cacheLegalGuardian.set(key, request$);
    }

    return this.cacheLegalGuardian.get(key)!;
  }

  create(data: CreateStudentRequestDto): Observable<any> {
    this.cachePage.clear()
    return this.apiService.post<CreateStudentRequestDto>(
      ApiConfig.endpoints.student.base,
      data
    ) as Observable<any>;
  }

  update(id: string, data: UpdateStudentRequestDto): Observable<any> {
    this.refreshCaches();
    return this.apiService.put<UpdateStudentRequestDto>(
      `${ApiConfig.endpoints.student.base}/${id}`,
      data
    ) as Observable<any>;
  }

  deleteById(id: string): Observable<any> {
    this.cachePage.clear();
    this.cacheLegalGuardian.clear();
    return this.apiService.delete(
      `${ApiConfig.endpoints.student.base}/${id}`
    ) as Observable<any>;
  }

  refreshCaches(): void {
    this.cachePage.clear();
    this.cacheDetail.clear();
    this.cacheLegalGuardian.clear();
  }
}
