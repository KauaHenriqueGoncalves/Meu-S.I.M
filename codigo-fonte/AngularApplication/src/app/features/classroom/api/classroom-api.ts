import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { PageResponse } from '../../../shared/models/page-response.model';
import { ApiConfig } from '../../../core/config/api.config';
import { ClassroomViewSimpleResponseDto } from '../dto/classroom-view-simple-response.dto';
import { Observable, shareReplay } from 'rxjs';
import { ClassroomDetailResponseDto } from '../dto/classroom-detail-response.dto';
import { CreateClassroomRequestDto } from '../dto/create-classroom-resquest.dto';
import { GetStudentIdInClassroomRequestDto } from '../dto/get-studentId-in-classroom-request.dto';
import { UpdateClassroomRequestDto } from '../dto/update-classroom-resquest.dto';

@Injectable({
  providedIn: 'root',
})
export class ClassroomApi {
  private cacheFindAll = new Map<string, Observable<PageResponse<ClassroomViewSimpleResponseDto>>>();
  private cacheDetail = new Map<string, Observable<ClassroomDetailResponseDto>>();
  private cacheAllByStudent = new Map<string, Observable<ClassroomViewSimpleResponseDto[]>>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) {
    this.cacheReset.register(() => this.cleanAllCaches());
  }

  findAll(page: number, size: number): Observable<PageResponse<ClassroomViewSimpleResponseDto>> {
    const key: string = `${page}-${size}`;

    if (!this.cacheFindAll.has(key)) {
      const $request = this.apiService.get<PageResponse<ClassroomViewSimpleResponseDto>>(
        `${ApiConfig.endpoints.classroom.base}?page=${page}&size=${size}`
      ).pipe(shareReplay(1)) as unknown as Observable<PageResponse<ClassroomViewSimpleResponseDto>>;
      this.cacheFindAll.set(key, $request);
    }

    return this.cacheFindAll.get(key)!;
  }

  findById(id: string): Observable<ClassroomDetailResponseDto> {
    const key: string = `${id}-detail`

    if (!this.cacheDetail.has(key)) {
      const $request = this.apiService.get<ClassroomDetailResponseDto>(
        `${ApiConfig.endpoints.classroom.base}/${id}`
      ).pipe(shareReplay(1)) as unknown as Observable<ClassroomDetailResponseDto>;
      this.cacheDetail.set(key, $request);
    }

    return this.cacheDetail.get(key)!;
  }

  findAllByStudentId(studentId: string): Observable<ClassroomViewSimpleResponseDto[]> {
    const key: string = `${studentId}-student`;

    if (!this.cacheAllByStudent.has(key)) {
      const $request = this.apiService.get<ClassroomViewSimpleResponseDto[]>(
        `${ApiConfig.endpoints.classroom.byStudent}/${studentId}`
      ).pipe(shareReplay(1)) as unknown as Observable<ClassroomViewSimpleResponseDto[]>;
      this.cacheAllByStudent.set(key, $request);
    }

    return this.cacheAllByStudent.get(key)!;
  }

  create(data: CreateClassroomRequestDto): Observable<any> {
    this.cacheFindAll.clear();
    this.cacheAllByStudent.clear();
    return this.apiService.post<CreateClassroomRequestDto>(
      ApiConfig.endpoints.classroom.base,
      data
    ) as Observable<any>;
  }

  addStudent(classroomId: string, data: GetStudentIdInClassroomRequestDto): Observable<any> {
    const endpoint: string = ApiConfig.endpoints.classroom.addStudent.replace('{id}', classroomId);
    this.cleanAllCaches();
    return this.apiService.post<GetStudentIdInClassroomRequestDto>(
      endpoint,
      data
    ) as Observable<any>;
  }

  removeStudent(classroomId: string, data: GetStudentIdInClassroomRequestDto): Observable<any> {
    const endpoint: string = ApiConfig.endpoints.classroom.removeStudent.replace('{id}', classroomId);
    this.cleanAllCaches();
    return this.apiService.post<GetStudentIdInClassroomRequestDto>(
      endpoint,
      data
    ) as Observable<any>;
  }

  update(id: string, data: UpdateClassroomRequestDto): Observable<any> {
    this.cleanAllCaches();
    return this.apiService.put<UpdateClassroomRequestDto>(
      `${ApiConfig.endpoints.classroom.base}/${id}`,
      data
    ) as Observable<any>;
  }

  deleteById(id: string): Observable<any> {
    return this.apiService.delete(
      `${ApiConfig.endpoints.classroom.base}/${id}`
    ) as Observable<any>;
  }

  cleanCacheDetails(): void {
    this.cacheDetail.clear();
  }

  cleanAllCaches(): void {
    this.cacheFindAll.clear();
    this.cacheDetail.clear();
    this.cacheAllByStudent.clear();
  }
}
