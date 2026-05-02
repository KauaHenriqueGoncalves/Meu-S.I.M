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

  get<T>(url: string, options?: any) {
    return this.http.get<T>(
      `${this.baseUrl}${url}`,
      {
        withCredentials: true,
        ...options
      }
    );
  }

  post<T>(url: string, body: any, options?: any) {
    return this.http.post<T>(
      `${this.baseUrl}${url}`,
      body,
      {
        withCredentials: true,
        ...options
      }
    );
  }

  patch<T>(url: string, body: any, options?: any) {
    return this.http.patch<T>(
      `${this.baseUrl}${url}`, 
      body,
      {
        withCredentials: true,
        ...options
      }
    );
  }

  delete<T>(url: string, options?: any) {
    return this.http.delete<T>(
      `${this.baseUrl}${url}`,
      {
        withCredentials: true,
        ...options
      }
    );
  }
}
