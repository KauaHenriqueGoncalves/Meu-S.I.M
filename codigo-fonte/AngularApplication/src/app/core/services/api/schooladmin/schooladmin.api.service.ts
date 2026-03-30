import { Injectable } from '@angular/core';
import { ApiService } from '../api.service';
import { UserRequest } from '../../../models/requests/user/user-request.model';
import { SchoolRequest } from '../../../models/requests/school/school-request.model';
import { ApiConfig } from '../../../config/api.config';
import { CaptchaRequest } from '../../../models/requests/captcha/capcha-request.model';
import { HttpClient, HttpHeaders } from '@angular/common/http';

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
    
    return this.api.post(ApiConfig.endpoints.schoolAdmin.base, payload);
  }
}
