import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { PhotoSvg } from '../../../../shared/components/svg/photo.svg';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { NoSpecialCharacteresDirective } from '../../../../shared/directives/no-special-characteres.directive';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';
import { PhoneOnlyDirective } from '../../../../shared/directives/phone-only.directive';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { SchooladminApi } from '../../api/schooladmin.api';
import { cpfValidator } from '../../../../shared/validation/cpf.validator';
import { timeout, catchError, throwError, finalize } from 'rxjs';
import { CreateSchoolAdminOwnSchoolRequestDTO } from '../../dto/create-school-admin-own-school.request.dto';

@Component({
  selector: 'app-create-school-admin',
  imports: [
    ReactiveFormsModule,
    NoEmojiDirective,
    NoSpecialCharacteresDirective,
    PhoneOnlyDirective,
    NumbersOnlyDirective,
    ArrowLeftSvg,
    PhotoSvg,
    SpinnerToButton
  ],
  templateUrl: './create-school-admin.html',
  styleUrl: './create-school-admin.sass',
})
export class CreateSchoolAdmin implements OnInit {
  adminForm!: FormGroup;
  isSubmitting = false;
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private schoolAdminApi: SchooladminApi,
    private router: Router,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.adminForm = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.maxLength(100)
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
      ]],
      cpf: ['', [
        Validators.required,
        cpfValidator()
      ]],
      phoneNumber: ['', [
        Validators.required,
        Validators.maxLength(20)
      ]],
      address: ['', [
        Validators.maxLength(100)
      ]]
    });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.adminForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  goBack(): void {
    this.router.navigate(['/app/school-admins']);
  }

  onSubmit(): void {
    if (this.adminForm.invalid) {
      this.adminForm.markAllAsTouched();
      this.notificationService.notify({
        type: 'warning',
        text: 'Por favor, preencha todos os campos obrigatórios corretamente.'
      });
      return;
    }

    this.isSubmitting = true;
    const formValue = this.adminForm.value;

    const payload: CreateSchoolAdminOwnSchoolRequestDTO = {
      username: formValue.username.trim(),
      email: formValue.email.trim(),
      password: formValue.password.trim(),
      cpf: formValue.cpf.trim(),
      phoneNumber: formValue.phoneNumber.trim(),
      address: formValue.address?.trim()
    };

    this.schoolAdminApi.createInSchool(payload)
      .pipe(
        timeout(10000),
        catchError((error) => throwError(() => error)),
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.notificationService.notify({
            type: 'success',
            text: 'Administrador criado com sucesso!'
          });
          this.router.navigate(['/app/school-admins']);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao criar administrador. Tente novamente.'
          });
        }
      });
  }
}
