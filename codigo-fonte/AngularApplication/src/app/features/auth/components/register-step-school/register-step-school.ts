import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';
import { cnpjValidator } from '../../../../shared/validation/cnpj.validator';
import { SchoolRequestDto } from '../../../school/dto/school-request.dto';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';

@Component({
  selector: 'app-register-step-school',
  imports: [ReactiveFormsModule, NumbersOnlyDirective, SpinnerToButton, NoEmojiDirective],
  templateUrl: './register-step-school.html',
  styleUrl: './register-step-school.sass',
})
export class RegisterStepSchool {
  @Input() isLoading: boolean = false;
  @Input() captchaExecuting: boolean = false;
  @Output() cancel = new EventEmitter<any>();
  @Output() next = new EventEmitter<SchoolRequestDto>();

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
    cnpj: new FormControl<string | null>('', [
      Validators.required,
      this.exactLength(14),
      cnpjValidator()
    ]),
    noCnpj: new FormControl(false),
    terms: new FormControl(false, [
      Validators.requiredTrue
    ])
  });

  constructor(
    private notificationService: NotificationService
  ) {
    this.form.get('noCnpj')?.valueChanges.subscribe((checked) => {
      const cnpjControl = this.form.get('cnpj');
      if (checked) {
        cnpjControl?.setValue(null);
        cnpjControl?.disable();
        cnpjControl?.clearValidators();
      } else {
        cnpjControl?.enable();
        cnpjControl?.setValidators([
          Validators.required,
          this.exactLength(14),
          cnpjValidator()
        ]);
      }
      cnpjControl?.updateValueAndValidity();
    });
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

  cancelForm(): void {
    this.cancel.emit();
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
      return;
    }

    const raw = this.form.getRawValue();

    const payload: SchoolRequestDto = {
      nameCode: raw.nameCode?.trim(),
      schoolName: raw.schoolName?.trim(),
      cnpj: raw.cnpj?.trim() || null
    }

    this.next.emit(payload);
  }
}
