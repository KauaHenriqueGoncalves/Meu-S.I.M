import { Injectable } from '@angular/core';
import { ApiConfig } from '../../config/api.config';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private baseUrl = ApiConfig.apiUrl;

  constructor(
    private http: HttpClient
  ) { }

  get(url: string) {
    return this.http.get(`${this.baseUrl}${url}`);
  }

  post(url: string, body: any, options?: any) {
    return this.http.post(
      `${this.baseUrl}${url}`,
      body,
      {
        withCredentials: true,
        ...options
      }
    );
  }

  patch(url: string, body: any) {
    return this.http.patch(`${this.baseUrl}${url}`, body);
  }

  delete(url: string) {
    return this.http.delete(`${this.baseUrl}${url}`);
  }
}
