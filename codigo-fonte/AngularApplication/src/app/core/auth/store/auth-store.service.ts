import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthStore {
  private readonly TOKEN_KEY = 'access_token';
  
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  setToken(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  clear() {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  isInitialized(): boolean {
    return this.getToken() !== null;
  }
}
