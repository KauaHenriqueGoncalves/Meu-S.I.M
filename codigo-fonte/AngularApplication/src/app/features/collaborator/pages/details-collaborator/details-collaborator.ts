import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { NoSpecialCharacteresDirective } from '../../../../shared/directives/no-special-characteres.directive';
import { PhoneOnlyDirective } from '../../../../shared/directives/phone-only.directive';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { PhotoSvg } from '../../../../shared/components/svg/photo.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { Router } from '@angular/router';
import { CollaboratorApi } from '../../api/collaborator.api';
import { CollaboratorDetailResponseDto } from '../../dto/collaborator-detail-response.dto';
import { UpdateCollaboratorRequestDto } from '../../dto/update-cpllaborator-request.dto';
import { finalize, timeout } from 'rxjs';
import { UserChangePasswordRequestDto } from '../../../user/dto/user-change-password-request.dto';
import { WhatsappSvg } from '../../../../shared/components/svg/whatsapp.svg';

@Component({
  selector: 'app-details-collaborator',
  imports: [
    ReactiveFormsModule,
    NoEmojiDirective,
    NoSpecialCharacteresDirective,
    PhoneOnlyDirective,
    NumbersOnlyDirective,
    ArrowLeftSvg,
    PhotoSvg,
    SpinnerToButton,
    WhatsappSvg
  ],
  templateUrl: './details-collaborator.html',
  styleUrl: './details-collaborator.sass',
})
export class DetailsCollaborator implements OnInit {
  collaboratorId!: string;
  editForm!: FormGroup;
  passwordForm!: FormGroup;

  private initialValues: any;

  isLoading = true;
  isSubmitting = false;
  isChangingPassword = false;
  showPassword = false;
  isDeleting = false;

  minDate = '1950-01-01';
  maxDate = new Date().toISOString().split('T')[0];

  currentPhone = '';

  showDeleteModal = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private collaboratorApi: CollaboratorApi,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.collaboratorId = history.state.id || '';
    if (!this.collaboratorId) {
      this.goBack();
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, tente novamente mais tarde'
      });
      return;
    }
    this.initForms();
    this.loadCollaborator();
  }

  getControl(name: string): FormControl {
    return this.editForm.get(name) as FormControl;
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
      cpf: [{ value: '', disabled: true }],
      phoneNumber: ['', [
        Validators.required,
        Validators.maxLength(20)
      ]],
      address: ['', [
        Validators.maxLength(100)
      ]],
      isActive: [true],
      dateOfBirth: ['', [
        Validators.required
      ]],
      specialty: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(30)
      ]],
      workload: ['', [
        Validators.required,
        Validators.pattern(/^(\d{1,2})h$/)
      ]]
    });

    this.passwordForm = this.fb.group({
      newPassword: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(20)
      ]]
    });
  }

  loadCollaborator(): void {
    this.collaboratorApi.findById(this.collaboratorId)
      .subscribe({
        next: (data: CollaboratorDetailResponseDto) => {
          this.currentPhone = data.phoneNumber;
          const formattedDate = new Date(data.dateOfBirth).toISOString().split('T')[0];
          this.editForm.patchValue({
            username: data.username,
            email: data.email,
            cpf: data.cpf,
            phoneNumber: data.phoneNumber,
            address: data.address,
            isActive: data.isActive,
            dateOfBirth: formattedDate,
            specialty: data.specialty,
            workload: data.workload
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
            text: 'Erro ao carregar colaborador.'
          });
          this.goBack();
        }
      });
  }

  openWhatsApp(): void {
    if (!this.currentPhone) {
      this.notificationService.notify({
        type: 'error',
        text: 'É necessário possui o número de telefone'
      });
      return;
    };
    // Remove tudo que não for número
    const numericPhone = this.currentPhone.replace(/\D/g, '');
    // Assume DDI 55 (Brasil) se não tiver
    const waNumber = numericPhone.startsWith('55') ? numericPhone : `55${numericPhone}`;
    window.open(`https://wa.me/${waNumber}`, '_blank');
  }

  isFieldInvalid(form: FormGroup, field: string): boolean {
    const control = form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  get isFormChanged(): boolean {
    if (!this.initialValues) return false;
    return JSON.stringify(this.editForm.getRawValue()) !== JSON.stringify(this.initialValues);
  }

  goBack(): void {
    this.router.navigate(['/app/collaborators']);
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

    const payload: UpdateCollaboratorRequestDto = {
      username: formValue.username.trim(),
      email: formValue.email.trim(),
      phoneNumber: formValue.phoneNumber.trim(),
      address: formValue.address?.trim(),
      isActive: formValue.isActive,
      dateOfBirth: new Date(formValue.dateOfBirth + 'T00:00:00'),
      specialty: formValue.specialty.trim(),
      workload: formValue.workload.trim()
    };

    this.collaboratorApi.update(this.collaboratorId, payload)
      .pipe(
        timeout(10000),
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.currentPhone = payload.phoneNumber;
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

    this.collaboratorApi.changePassword(this.collaboratorId, payload)
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

  deleteCollaborator(): void {
    this.isDeleting = true;

    this.collaboratorApi.deleteById(this.collaboratorId)
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
            text: 'Colaborador excluído com sucesso!'
          });
          this.router.navigate(['/app/collaborators']);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao excluir o colaborador.'
          });
        }
      });
  }
}
