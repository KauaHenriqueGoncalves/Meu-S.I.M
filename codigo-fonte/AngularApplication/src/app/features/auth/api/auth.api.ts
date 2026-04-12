import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { LoginRequestDto } from '../dto/login-request.dto';
import { CaptchaRequestDto } from '../dto/capcha-request.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { HttpContext } from '@angular/common/http';
import { NO_AUTH } from '../../../core/config/no-auth.token.config';

@Injectable({
  providedIn: 'root',
})
export class AuthApi {
  constructor(
    private apiService: ApiService
  ) { }

  login(loginRequest: LoginRequestDto, captchaRequest: CaptchaRequestDto) {
    const payload = {
      schoolCode: loginRequest.schoolCode,
      email: loginRequest.email,
      password: loginRequest.password,
      captchaRequest: {
        captchaToken: captchaRequest.captchaToken
      }
    }

    return this.apiService.post(
      ApiConfig.endpoints.auth.login, 
      payload, 
      {
        context: new HttpContext().set(NO_AUTH, true)
      }
    );
  }

  refresh() {
    return this.apiService.post(
      ApiConfig.endpoints.auth.refresh, 
      {}, 
      {
        context: new HttpContext().set(NO_AUTH, true)
      }
    );
  }

  logout(): void {
    this.apiService.post(
      ApiConfig.endpoints.auth.logout,
      {},
      {
        context: new HttpContext().set(NO_AUTH, true)
      }
    );
  }
}
