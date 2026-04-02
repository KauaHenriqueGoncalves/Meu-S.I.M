import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-verify-account-failed',
  imports: [],
  templateUrl: './verify-account-failed.html',
  styleUrl: './verify-account-failed.sass',
})
export class VerifyAccountFailed implements OnInit {
  reason: string | null = null;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.reason = this.activatedRoute.snapshot.queryParamMap.get('reason');
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }

  get message(): string {
    switch (this.reason) {
      case 'expired':
        return 'Este link de confirmação expirou.';
      case 'invalid':
        return 'O link de confirmação é inválido.';
      default:
        return 'Não conseguimos confirmar seu email.';
    }
  }
}
