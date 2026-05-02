import { Component, EventEmitter, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { cpfValidator } from '../../../../shared/validation/cpf.validator';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { UserRequestDto } from '../../../user/dto/user-request.dto';
import { PhoneOnlyDirective } from '../../../../shared/directives/phone-only.directive';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';

@Component({
  selector: 'app-register-step-user',
  imports: [ReactiveFormsModule, NumbersOnlyDirective, PhoneOnlyDirective, NoEmojiDirective],
  templateUrl: './register-step-user.html',
  styleUrl: './register-step-user.sass',
})
export class RegisterStepUser {
  @Output() next = new EventEmitter<UserRequestDto>();

  form = new FormGroup({
    name: new FormControl('', [
      Validators.required,
      Validators.maxLength(100)
    ]),

    email: new FormControl('', [
      Validators.required,
      Validators.email,
      Validators.maxLength(255)
    ]),

    confirmEmail: new FormControl('', [
      Validators.required,
      Validators.email,
      Validators.maxLength(255)
    ]),

    cpf: new FormControl('', [
      Validators.required,
      this.exactLength(11),
      cpfValidator()
    ]),

    phoneNumber: new FormControl('', [
      Validators.required,
      Validators.maxLength(20)
    ]),

    address: new FormControl('', [
      Validators.maxLength(100)
    ]),

    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20)
    ]),

    confirmPassword: new FormControl('', [
      Validators.required
    ]),
  }, {
    validators: [
      this.passwordMatchValidator(),
      this.emailMatchValidator()
    ]
  });

  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private notificationService: NotificationService
  ) { }

  passwordMatchValidator(): ValidatorFn {
    return (form: AbstractControl) => {
      const password = form.get('password')?.value;
      const confirm = form.get('confirmPassword')?.value;

      if (password !== confirm) {
        return { passwordMismatch: true };
      }

      return null;
    };
  }

  emailMatchValidator(): ValidatorFn {
    return (form: AbstractControl) => {
      const email = form.get('email')?.value;
      const confirm = form.get('confirmEmail')?.value;

      if (email != confirm) {
        return { emailMismatch: true }
      }

      return null;
    }
  }

  exactLength(length: number) {
    return (control: AbstractControl) => {
      const value = control.value || '';
      return value.length === length ? null : { exactLength: { requiredLength: length, actualLength: value.length } };
    };
  }

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  inputsEmpty(): void {
    this.notificationService.notify({
      type: 'error',
      text: 'Preencha todos os campos corretamente'
    });
  }

  submit(): void {
    this.form.markAllAsTouched();

    if (this.form.invalid) {
      this.inputsEmpty();
    }

    const payload: UserRequestDto = {
      username: this.form.value.name?.trim(),
      email: this.form.value.email?.trim(),
      password: this.form.value.password?.trim(),
      cpf: this.form.value.cpf?.trim(),
      phoneNumber: this.form.value.phoneNumber?.trim(),
      address: this.form.value.address?.trim()
    };

    this.next.emit(payload);
  }
}
