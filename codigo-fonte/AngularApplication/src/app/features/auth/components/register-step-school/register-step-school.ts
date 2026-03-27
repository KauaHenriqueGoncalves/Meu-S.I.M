import { Component, EventEmitter, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';
import { cnpjValidator } from '../../../../shared/validation/cnpj.validator';
import { SchoolRequest } from '../../models/school-request.model';

@Component({
  selector: 'app-register-step-school',
  imports: [ReactiveFormsModule, NumbersOnlyDirective],
  templateUrl: './register-step-school.html',
  styleUrl: './register-step-school.sass',
})
export class RegisterStepSchool {
  @Output() cancel = new EventEmitter<any>();
  @Output() next = new EventEmitter<any>();

  form = new FormGroup({
    nameCode: new FormControl('', [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(50)
    ]),

    schoolName: new FormControl('', [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(50)
    ]),

    cnpj: new FormControl('', [
      Validators.required,
      this.exactLength(14),
      cnpjValidator()
    ])
  });

  constructor(
    private notificationService: NotificationService
  ) { }

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

  cancelForm(): void {
    this.cancel.emit();
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

    const payload: SchoolRequest = {
      nameCode: this.form.value.nameCode,
      schoolName: this.form.value.schoolName,
      cnpj: this.form.value.cnpj
    };

    this.next.emit(payload);
  }
}
