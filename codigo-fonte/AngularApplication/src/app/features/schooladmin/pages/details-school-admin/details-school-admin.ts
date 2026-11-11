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
import { timeout, finalize } from 'rxjs';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { UserChangePasswordRequestDto } from '../../../user/dto/user-change-password-request.dto';
import { SchooladminApi } from '../../api/schooladmin.api';
import { SchoolAdminDetailViewResponseDTO } from '../../dto/school-admin-detail-view.response.dto';
import { UpdateSchoolAdminRequestDTO } from '../../dto/update-school-admin.request.dto';

@Component({
  selector: 'app-details-school-admin',
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
  templateUrl: './details-school-admin.html',
  styleUrl: './details-school-admin.sass',
})
export class DetailsSchoolAdmin implements OnInit {
  adminId!: string;
  editForm!: FormGroup;
  passwordForm!: FormGroup;

  private initialValues: any;

  isLoading = true;
  isSubmitting = false;
  isChangingPassword = false;
  showPassword = false;
  isDeleting = false;
  showDeleteModal = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private schoolAdminApi: SchooladminApi,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.adminId = history.state.id || '';
    if (!this.adminId) {
      this.goBack();
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, tente novamente mais tarde'
      });
      return;
    }
    this.initForms();
    this.loadAdmin();
  }

  get isFormChanged(): boolean {
    if (!this.initialValues) return false;
    return JSON.stringify(this.editForm.getRawValue()) !== JSON.stringify(this.initialValues);
  }

  initForms(): void {
    this.editForm = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.maxLength(100)
      ]],
      email: ['', [
        Validators.required,
        Validators.email,
        Validators.maxLength(255)
      ]],
      cpf: [{
        value: '',
        disabled: true
      }],
      phoneNumber: ['', [
        Validators.required,
        Validators.maxLength(20)
      ]],
      address: ['', [
        Validators.maxLength(100)
      ]],
      isActive: [true]
    });

    this.passwordForm = this.fb.group({
      newPassword: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(20)
      ]]
    });
  }

  loadAdmin(): void {
    this.schoolAdminApi.getById(this.adminId)
      .subscribe({
        next: (data: SchoolAdminDetailViewResponseDTO) => {
          this.editForm.patchValue({
            username: data.username,
            email: data.email,
            cpf: data.cpf,
            phoneNumber: data.phoneNumber,
            address: data.address,
            isActive: data.isActive
          });
          this.editForm.markAsPristine();
          this.editForm.markAsUntouched();
          this.initialValues = this.editForm.getRawValue();
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.notificationService.notify({
            type: 'error',
            text: 'Erro ao carregar administrador.'
          });
          this.goBack();
        }
      });
  }

  isFieldInvalid(form: FormGroup, field: string): boolean {
    const control = form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  goBack(): void {
    window.history.back();
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

    const payload: UpdateSchoolAdminRequestDTO = {
      username: formValue.username.trim(),
      email: formValue.email.trim(),
      phoneNumber: formValue.phoneNumber.trim(),
      address: formValue.address?.trim(),
      isActive: formValue.isActive
    };

    this.schoolAdminApi.update(this.adminId, payload)
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
            text: 'Dados atualizados com sucesso!'
          });
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao atualizar.'
          });
        }
      });
  }

  onChangePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    this.isChangingPassword = true;
    const payload: UserChangePasswordRequestDto = {
      newPassword: this.passwordForm.value.newPassword.trim()
    };

    this.schoolAdminApi.changePassword(this.adminId, payload)
      .pipe(
        finalize(() => {
          this.isChangingPassword = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.notificationService.notify({
            type: 'success',
            text: 'Senha alterada com sucesso!'
          });
          this.passwordForm.reset();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao alterar senha.'
          });
        }
      });
  }

  deleteAdmin(): void {
    this.isDeleting = true;
    this.schoolAdminApi.delete(this.adminId)
      .pipe(
        finalize(() => {
          this.isDeleting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.notificationService.notify({ 
            type: 'success', 
            text: 'Administrador excluído com sucesso!' 
          });
          this.router.navigate(['/app/school-admins']);
        },
        error: (err) => {
          this.notificationService.notify({ 
            type: 'error', 
            text: err.error?.message || 'Erro ao excluir o administrador.' 
          });
        }
      });
  }
}
