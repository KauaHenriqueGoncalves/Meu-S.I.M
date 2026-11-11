import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { SchoolApi } from '../../../school/api/school-api';
import { cnpjValidator } from '../../../../shared/validation/cnpj.validator';
import { timeout, catchError, throwError, finalize } from 'rxjs';
import { SchoolResponseDto } from '../../../school/dto/school-response.dto';
import { UpdateSchoolRequestDto } from '../../../school/dto/update-school.request.dto';

@Component({
  selector: 'app-edit-school',
  imports: [
    ReactiveFormsModule,
    NumbersOnlyDirective,
    NoEmojiDirective,
    SpinnerToButton,
    ArrowLeftSvg
  ],
  templateUrl: './edit-school.html',
  styleUrl: './edit-school.sass',
})
export class EditSchool implements OnInit {
  schoolId!: string;
  editForm!: FormGroup;

  private initialValues: any;

  isLoading = true;
  isSubmitting = false;

  initialCnpjValue: string | null = null;

  constructor(
    private fb: FormBuilder,
    private schoolApi: SchoolApi,
    private router: Router,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.loadSchool();
  }

  get isFormChanged(): boolean {
    if (!this.initialValues) return false;
    return JSON.stringify(this.editForm.getRawValue()) !== JSON.stringify(this.initialValues);
  }

  initForm(): void {
    this.editForm = this.fb.group({
      schoolName: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(50)
      ]],
      nameCode: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(50)
      ]],
      cnpj: ['', [
        Validators.required,
        cnpjValidator()
      ]],
      noCnpj: [false]
    });

    let initializing = true;

    this.editForm.get('noCnpj')?.valueChanges.subscribe((checked) => {
      const cnpjControl = this.editForm.get('cnpj');
      if (checked) {
        cnpjControl?.setValue(null);
        cnpjControl?.disable();
        cnpjControl?.clearValidators();
      } else {
        cnpjControl?.enable();
        cnpjControl?.setValidators([Validators.required, cnpjValidator()]);
        if (!initializing) {
          cnpjControl?.setValue(this.initialCnpjValue);
        }
        console.log(initializing  + " - " + this.initialCnpjValue);
      }
      cnpjControl?.updateValueAndValidity();
      initializing = false;
      this.cdr.detectChanges();
    });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.editForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  loadSchool(): void {
    this.schoolApi.mySchool()
      .pipe(
        timeout(10000),
        catchError((error) => throwError(() => error)),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: SchoolResponseDto) => {
          this.schoolId = res.id;
          this.editForm.patchValue({
            schoolName: res.schoolName,
            nameCode: res.nameCode,
            cnpj: res.cnpj,
            noCnpj: !res.cnpj
          });
          this.initialCnpjValue = res.cnpj;
          this.editForm.markAsPristine();
          this.editForm.markAsUntouched();
          this.initialValues = this.editForm.getRawValue();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar os dados da escola.'
          });
          this.goBack();
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/app/configurations']);
  }

  onUpdateDetails(): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      this.notificationService.notify({
        type: 'warning',
        text: 'Verifique os campos inválidos.'
      });
      return;
    }

    this.isSubmitting = true;
    const formValue = this.editForm.getRawValue();

    const payload: UpdateSchoolRequestDto = {
      schoolName: formValue.schoolName.trim(),
      nameCode: formValue.nameCode.trim(),
      cnpj: formValue.cnpj?.trim() || null
    };

    this.schoolApi.update(this.schoolId, payload)
      .pipe(
        timeout(10000),
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.editForm.markAsPristine();
          this.editForm.markAsUntouched();
          this.initialValues = this.editForm.getRawValue();
          this.notificationService.notify({
            type: 'success',
            text: 'Dados da escola atualizados com sucesso!'
          });
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao atualizar os dados da escola.'
          });
        }
      });
  }
}
