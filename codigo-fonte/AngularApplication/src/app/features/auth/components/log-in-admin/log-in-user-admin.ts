import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { AdminLoginRequestDto } from '../../dto/admin-login-request.dto';
import { cpfValidator } from '../../../../shared/validation/cpf.validator';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';

@Component({
  selector: 'app-log-in-user-admin',
  imports: [
    ReactiveFormsModule,
    SpinnerToButton,
    NoEmojiDirective,
    NumbersOnlyDirective
  ],
  templateUrl: './log-in-user-admin.html',
  styleUrl: './log-in-user-admin.sass',
})
export class LogInUserAdmin implements OnInit {
  @Input() isLoading: boolean = false;
  @Input() captchaExecuting: boolean = false;
  @Output() next = new EventEmitter<AdminLoginRequestDto>();

  loginForm!: FormGroup;

  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private notificationService: NotificationService
  ) { }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.loginForm = this.fb.group({
      cpf: ['', [
        Validators.required,
        cpfValidator()
      ]],
      email: ['', [
        Validators.required,
        Validators.email,
        Validators.maxLength(255)
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(20)
      ]]
    });
  }

  isFieldInvalid(group: FormGroup, field: string): boolean {
    const control = group.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  submit(): void {
    this.loginForm.markAllAsTouched();
    if (this.loginForm.invalid) {
      this.notificationService.notify({
        type: 'error',
        text: 'Preencha todos os campos corretamente'
      });
      return;
    }
    const formValue = this.loginForm.value;
    const payload: AdminLoginRequestDto = {
      cpf: formValue.cpf!.trim(),
      email: formValue.email!.trim(),
      password: formValue.password!.trim()
    }
    this.next.emit(payload);
  }
}
