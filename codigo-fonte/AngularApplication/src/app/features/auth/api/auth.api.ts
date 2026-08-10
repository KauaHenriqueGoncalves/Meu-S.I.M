import { Injectable } from '@angular/core';
import { ApiService } from '../../../core/services/api/api.service';
import { LoginRequestDto } from '../dto/login-request.dto';
import { CaptchaRequestDto } from '../dto/capcha-request.dto';
import { ApiConfig } from '../../../core/config/api.config';
import { HttpContext } from '@angular/common/http';
import { NO_AUTH } from '../../../core/config/no-auth.token.config';
import { TokenResponse } from '../dto/token-response.dto';
import { Observable } from 'rxjs';
import { AdminLoginRequestDto } from '../dto/admin-login-request.dto';

@Injectable({
  providedIn: 'root',
})
export class AuthApi {
  constructor(
    private apiService: ApiService
  ) { }

  login(loginRequest: LoginRequestDto, captchaRequest: CaptchaRequestDto): Observable<TokenResponse> {
    const payload = {
      schoolCode: loginRequest.schoolCode,
      email: loginRequest.email,
      password: loginRequest.password,
      captchaRequest: {
        captchaToken: captchaRequest.captchaToken
      }
    }

    return this.apiService.post<TokenResponse>(
      ApiConfig.endpoints.auth.login, 
      payload, 
      {
        context: new HttpContext().set(NO_AUTH, true)
      }
    ) as unknown as Observable<TokenResponse>;
  }

  loginAdmin(adminLoginRequest: AdminLoginRequestDto, captchaRequest: CaptchaRequestDto): Observable<TokenResponse> {
    const payload = { ...adminLoginRequest, captchaRequest };
    console.log(payload);
    return this.apiService.post<TokenResponse>(
      ApiConfig.endpoints.auth.loginAdmin, 
      payload, 
      {
        context: new HttpContext().set(NO_AUTH, true)
      }
    ) as unknown as Observable<TokenResponse>;
  }

  refresh(): Observable<TokenResponse> {
    return this.apiService.post<TokenResponse>(
      ApiConfig.endpoints.auth.refresh, 
      {}, 
      {
        context: new HttpContext().set(NO_AUTH, true)
      }
    ) as unknown as Observable<TokenResponse>;
  }

  logout(): void {
    this.apiService.post<any>(
      ApiConfig.endpoints.auth.logout,
      {},
      {
        context: new HttpContext().set(NO_AUTH, true)
      }
    );
  }
}
