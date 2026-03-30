import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class RegisterStateService {
  email: string | null = null;

  clear() {
    this.email = null;
  }
}
