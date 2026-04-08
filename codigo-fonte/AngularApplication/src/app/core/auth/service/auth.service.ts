import { Injectable } from '@angular/core';
import { AuthStore } from '../store/auth-store.service';
import { TokenPayload } from '../payload/token.payload';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(
    private authStore: AuthStore,
  ) { }

  getToken(): string | null {
    return this.authStore.getToken();
  }
  
  getPayload(): TokenPayload | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      return jwtDecode<TokenPayload>(token);
    } catch {
      return null;
    }
  }
}
