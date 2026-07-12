import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { PageResponse } from '../../../shared/models/page-response.model';
import { CollaboratorViewSimplesResponseDto } from '../dto/collaborator-view-simple-response.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { Observable, shareReplay } from 'rxjs';
import { CollaboratorDetailResponseDto } from '../dto/collaborator-detail-response.dto';
import { CreateCollaboratorRequestDto } from '../dto/create-collaborator-request.dto';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { UpdateCollaboratorRequestDto } from '../dto/update-collaborator-request.dto';
import { UserChangePasswordRequestDto } from '../../user/dto/user-change-password-request.dto';

@Injectable({
  providedIn: 'root',
})
export class CollaboratorApi {
  private cacheViewSimple = new Map<string, Observable<PageResponse<CollaboratorViewSimplesResponseDto>>>();
  private cacheDetail = new Map<string, Observable<CollaboratorDetailResponseDto>>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService
  ) {
    this.cacheReset.register(() => this.refreshAllCaches());
  }

  findAll(name: string, page: number, size: number): Observable<PageResponse<CollaboratorViewSimplesResponseDto>> {
    const key: string = `${name}-${page}-${size}`;
    
    if (!this.cacheViewSimple.has(key)) {
      const request$ = this.apiService.get<PageResponse<CollaboratorViewSimplesResponseDto>>(
        `${ApiConfig.endpoints.collaborator.base}?name=${name}&page=${page}&size=${size}`
      ).pipe(shareReplay(1)) as unknown as Observable<PageResponse<CollaboratorViewSimplesResponseDto>>;
      this.cacheViewSimple.set(key, request$);
    }

    return this.cacheViewSimple.get(key)!;
  }

  findById(id: string): Observable<CollaboratorDetailResponseDto> {
    const key: string = `${id}-detail`;

    if (!this.cacheDetail.has(key)) {
      const request$ = this.apiService.get<CollaboratorDetailResponseDto>(
        `${ApiConfig.endpoints.collaborator.base}/${id}`
      ).pipe(shareReplay(1)) as unknown as Observable<CollaboratorDetailResponseDto>;
      this.cacheDetail.set(key, request$);
    }

    return this.cacheDetail.get(key)!;
  }

  create(data: CreateCollaboratorRequestDto): Observable<any> {    
    this.cacheViewSimple.clear();
    return this.apiService.post(
      ApiConfig.endpoints.collaborator.base,
      data
    ) as Observable<any>;
  }

  update(id: string, data: UpdateCollaboratorRequestDto): Observable<any> {
    this.refreshAllCaches();
    return this.apiService.put(
      `${ApiConfig.endpoints.collaborator.base}/${id}`,
      data
    ) as Observable<any>;
  }

  changePassword(id: string, data: UserChangePasswordRequestDto): Observable<any> {
    const endpoint: string = ApiConfig.endpoints.collaborator.changePassword.replace('{id}', id);
    this.cacheDetail.clear();
    return this.apiService.patch(
      endpoint,
      data
    ) as Observable<any>;
  }

  deleteById(id: string): Observable<any> {
    this.cacheViewSimple.clear();
    return this.apiService.delete(
      `${ApiConfig.endpoints.collaborator.base}/${id}`
    ) as Observable<any>;
  }

  refreshAllCaches(): void {
    this.cacheViewSimple.clear();
    this.cacheDetail.clear();
  }
}
