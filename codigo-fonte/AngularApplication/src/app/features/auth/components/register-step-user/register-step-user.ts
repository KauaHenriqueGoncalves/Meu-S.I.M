import { Component, EventEmitter, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { cpfValidator } from '../../../../shared/validation/cpf.validator';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { UserRequest } from '../../models/user-request.model';
import { PhoneOnlyDirective } from '../../../../shared/directives/phone-only.directive';

@Component({
  selector: 'app-register-step-user',
  imports: [ReactiveFormsModule, NumbersOnlyDirective, PhoneOnlyDirective],
  templateUrl: './register-step-user.html',
  styleUrl: './register-step-user.sass',
})
export class RegisterStepUser {
  @Output() next = new EventEmitter<any>();

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
    validators: this.passwordMatchValidator()
  });

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

  submit() {
    this.form.markAllAsTouched();

    if (this.form.invalid) {
      this.inputsEmpty();
    }

    const payload: UserRequest = {
      username: this.form.value.name,
      email: this.form.value.email,
      password: this.form.value.password,
      cpf: this.form.value.cpf,
      phoneNumber: this.form.value.phoneNumber,
      address: this.form.value.address
    };

    this.next.emit(payload);
  }
}
