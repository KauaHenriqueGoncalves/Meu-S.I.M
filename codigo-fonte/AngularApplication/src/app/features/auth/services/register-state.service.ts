import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class RegisterStateService {
  email: string | null | undefined = null;

  clear() {
    this.email = null;
  }
}
