import { Injectable } from '@angular/core';
import { HttpContext } from '@angular/common/http';
import { ApiService } from '../../../core/services/api/api.service';
import { UserRequestDto } from '../../user/dto/user-request.dto';
import { SchoolRequestDto } from '../../school/dto/school-request.dto';
import { CaptchaRequestDto } from '../../auth/dto/capcha-request.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { NO_AUTH } from '../../../core/config/no-auth.token.config';
import { SchoolAdminSimpleViewResponseDTO } from '../dto/school-admin-simple-view.response.dto';
import { Observable, shareReplay } from 'rxjs';
import { SchoolAdminDetailViewResponseDTO } from '../dto/school-admin-detail-view.response.dto';
import { CreateSchoolAdminOwnSchoolRequestDTO } from '../dto/create-school-admin-own-school.request.dto';
import { UpdateSchoolAdminRequestDTO } from '../dto/update-school-admin.request.dto';
import { CacheResetService } from '../../../core/services/cache-reset/cache-reset.service';
import { UserChangePasswordRequestDto } from '../../user/dto/user-change-password-request.dto';

@Injectable({
  providedIn: 'root',
})
export class SchooladminApi {
  private cache = new Map<string, any>();

  constructor(
    private apiService: ApiService,
    private cacheReset: CacheResetService,
  ) {
    this.cacheReset.register(() => this.cache.clear());
  }

  create(userRequest: UserRequestDto, schoolRequest: SchoolRequestDto, captchaRequest: CaptchaRequestDto) {
    const payload = {
      user: userRequest,
      school: schoolRequest,
      captcha: {
        token: captchaRequest.captchaToken
      }
    };
    return this.apiService.post(
      `${ApiConfig.endpoints.schoolAdmin.base}/new-school`,
      payload,
      {
        context: new HttpContext().set(NO_AUTH, true)
      }
    );
  }

  page(name: string, page: number, size: number): Observable<SchoolAdminSimpleViewResponseDTO> {
    const key: string = `${name}-${page}-${size}-page`;
    if (!this.cache.has(key)) {
      const request$ = this.apiService.get(
        `${ApiConfig.endpoints.schoolAdmin.base}?name=${name}&page=${page}&size=${size}`
      ).pipe(shareReplay(1)) as unknown as Observable<SchoolAdminSimpleViewResponseDTO>;
      this.cache.set(key, request$);
    }
    return this.cache.get(key)!;
  }

  getById(id: string): Observable<SchoolAdminDetailViewResponseDTO> {
    const key: string = `${id}-detail`;
    if (!this.cache.has(key)) {
      const request$ = this.apiService.get(
        `${ApiConfig.endpoints.schoolAdmin.base}/${id}`
      ).pipe(shareReplay(1)) as unknown as Observable<SchoolAdminDetailViewResponseDTO>;
      this.cache.set(key, request$);
    }
    return this.cache.get(key)!;
  }

  createInSchool(payload: CreateSchoolAdminOwnSchoolRequestDTO): Observable<any> {
    this.cache.clear();
    return this.apiService.post(
      ApiConfig.endpoints.schoolAdmin.base,
      payload
    ) as Observable<any>;
  }

  update(id: string, payload: UpdateSchoolAdminRequestDTO): Observable<any> {
    this.cache.clear();
    return this.apiService.put(
      `${ApiConfig.endpoints.schoolAdmin.base}/${id}`,
      payload
    ) as Observable<any>;
  }

  changePassword(id: string, data: UserChangePasswordRequestDto): Observable<any> {
    return this.apiService.put(
      `${ApiConfig.endpoints.schoolAdmin.base}/${id}/password`,
      data
    ) as Observable<any>;
  }

  delete(id: string): Observable<any> {
    this.cache.clear();
    return this.apiService.delete(
      `${ApiConfig.endpoints.schoolAdmin.base}/${id}`
    ) as Observable<any>;
  }
}
