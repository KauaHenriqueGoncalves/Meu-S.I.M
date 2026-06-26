import { Injectable } from '@angular/core';
import { HttpContext } from '@angular/common/http';
import { ApiService } from '../../../core/services/api/api.service';
import { UserRequestDto } from '../../user/dto/user-request.dto';
import { SchoolRequestDto } from '../../school/dto/school-request.dto';
import { CaptchaRequestDto } from '../../auth/dto/capcha-request.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { NO_AUTH } from '../../../core/config/no-auth.token.config';

@Injectable({
  providedIn: 'root',
})
export class SchooladminApi {
  constructor(
    private apiService: ApiService,
  ) { }

  create(userRequest: UserRequestDto, schoolRequest: SchoolRequestDto, captchaRequest: CaptchaRequestDto) {
    const payload = {
      userRequest: userRequest,
      schoolRequest: schoolRequest,
      captchaRequest: captchaRequest
    };

    return this.apiService.post(
      ApiConfig.endpoints.schoolAdmin.base,
      payload,
      {
        context: new HttpContext().set(NO_AUTH, true)
      }
    );
  }
}
