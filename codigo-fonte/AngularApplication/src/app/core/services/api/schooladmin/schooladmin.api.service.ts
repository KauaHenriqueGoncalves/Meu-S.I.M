import { Injectable } from '@angular/core';
import { ApiService } from '../api.service';
import { UserRequest } from '../../../../features/auth/data/user-request.model';
import { SchoolRequest } from '../../../../features/auth/data/school-request.model';
import { ApiConfig } from '../../../config/api.config';
import { CaptchaRequest } from '../../../../features/auth/data/capcha-request.model';
import { HttpClient, HttpContext, HttpHeaders } from '@angular/common/http';
import { NO_AUTH } from '../../../config/no-auth.token.config';

@Injectable({
  providedIn: 'root',
})
export class SchooladminApiService {
  constructor(
    private api: ApiService,
  ) { }

  create(userRequest: UserRequest, schoolRequest: SchoolRequest, captchaRequest: CaptchaRequest) {
    const payload = {
      userRequest: userRequest,
      schoolRequest: schoolRequest,
      captchaRequest: captchaRequest
    };
    
    return this.api.post(ApiConfig.endpoints.schoolAdmin.base, payload, {
      context: new HttpContext().set(NO_AUTH, true)
    });
  }
}
