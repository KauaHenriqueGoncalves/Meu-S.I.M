import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-verify-account-success',
  imports: [],
  templateUrl: './verify-account-success.html',
  styleUrl: './verify-account-success.sass',
})
export class VerifyAccountSuccess {
  constructor(
    private router: Router
  ) { }

  goToLogin(): void {
    this.router.navigate(['/auth/log-in']);
  }
}
