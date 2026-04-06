import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { SpinnerToButton } from "../../../../shared/components/spinner-to-button/spinner-to-button";
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { Router, RouterLink } from '@angular/router';
import { LoginRequest } from '../../data/login-request.model';

@Component({
  selector: 'app-log-in-user',
  imports: [ReactiveFormsModule, SpinnerToButton, RouterLink],
  templateUrl: './log-in-user.html',
  styleUrl: './log-in-user.sass',
})
export class LogInUser {
  @Input() isLoading: boolean = false;
  @Input() captchaExecuting: boolean = false;
  @Output() next = new EventEmitter<LoginRequest>();
  
  form = new FormGroup({
    schoolCode: new FormControl('', [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(50)
    ]),

    email: new FormControl('', [
      Validators.required,
      Validators.email,
      Validators.maxLength(255)
    ]),

    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20)
    ])
  });

  constructor(
    private router: Router,
    private notificationService: NotificationService
  ) { }

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  toCreateAccount(): void {
    this.router.navigate(['/auth/sign-up']);
  }

  submit(): void {
    this.form.markAllAsTouched();

    if (this.form.invalid) {
      this.notificationService.notify({
        type: 'error',
        text: 'Preencha todos os campos corretamente'
      });
      return;
    }

    const payload: LoginRequest = {
      schoolCode: this.form.value.schoolCode!.trim(),
      email: this.form.value.email!.trim(),
      password: this.form.value.password!.trim()
    };

    this.next.emit(payload);
  }
}
