import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterStateService } from '../../services/register-state.service';

@Component({
  selector: 'app-verify-account',
  imports: [],
  templateUrl: './verify-account.html',
  styleUrl: './verify-account.sass',
})
export class VerifyAccount implements OnInit, OnDestroy {
  email: string | null = 'null';

  constructor(
    private router: Router,
    private registerStateService: RegisterStateService
  ) { }

  ngOnInit(): void {
    if (this.registerStateService.email != null) {
      this.email = this.registerStateService.email;
    }
  }
  
  ngOnDestroy(): void {
    this.registerStateService.clear();
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }
}
