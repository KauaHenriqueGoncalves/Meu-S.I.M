import { Injectable } from '@angular/core';
import { ApiService } from '../api.service';
import { LoginRequest } from '../../../../features/auth/data/login-request.model';
import { CaptchaRequest } from '../../../../features/auth/data/capcha-request.model';
import { ApiConfig } from '../../../config/api.config';
import { HttpContext } from '@angular/common/http';
import { NO_AUTH } from '../../../config/no-auth.token.config';

@Injectable({
  providedIn: 'root',
})
export class AuthApiService {
  constructor(
    private api: ApiService
  ) { }

  login(loginRequest: LoginRequest, captchaRequest: CaptchaRequest) {
    const payload = {
      schoolCode: loginRequest.schoolCode,
      email: loginRequest.email,
      password: loginRequest.password,
      captchaRequest: {
        captchaToken: captchaRequest.captchaToken
      }
    }

    // REQUISIÇÃO PROTEGIDA: return this.api.post(ApiConfig.endpoints.auth.login, payload);

    return this.api.post(ApiConfig.endpoints.auth.login, payload, {
      context: new HttpContext().set(NO_AUTH, true)
    });
  }

  refresh() {
    return this.api.post(ApiConfig.endpoints.auth.refresh, {}, {
      context: new HttpContext().set(NO_AUTH, true)
    });
  }
}
