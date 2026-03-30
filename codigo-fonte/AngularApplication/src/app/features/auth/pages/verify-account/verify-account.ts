import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterStateService } from '../../services/register-state.service';

@Component({
  selector: 'app-verify-account',
  imports: [],
  templateUrl: './verify-account.html',
  styleUrl: './verify-account.sass',
})
export class VerifyAccount implements OnDestroy {
  email: string | null = 'null';

  constructor(
    private router: Router,
    private registerStateService: RegisterStateService
  ) {
    if (registerStateService.email != null) {
      this.email = registerStateService.email;
    }
  }
  
  ngOnDestroy(): void {
    this.registerStateService.clear();
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }
}
