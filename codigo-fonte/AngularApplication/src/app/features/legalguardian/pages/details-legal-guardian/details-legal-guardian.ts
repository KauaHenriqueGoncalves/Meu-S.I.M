import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { NoSpecialCharacteresDirective } from '../../../../shared/directives/no-special-characteres.directive';
import { PhoneOnlyDirective } from '../../../../shared/directives/phone-only.directive';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { PhotoSvg } from '../../../../shared/components/svg/photo.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { WhatsappSvg } from '../../../../shared/components/svg/whatsapp.svg';
import { LegalGuardianApi } from '../../api/legal-guardian-api';
import { UpdateLegalGuardianRequestDto } from '../../dto/update-legal-guardian-request.dto';
import { UserChangePasswordRequestDto } from '../../../user/dto/user-change-password-request.dto';
import { LegalGuardianDetailResponseDto } from '../../dto/legal-guardian-detail-response.dto';
import { finalize, timeout } from 'rxjs';
import { UploadSvg } from '../../../../shared/components/svg/upload.svg';
import { FileSvg } from '../../../../shared/components/svg/file.svg';
import { TrashSvg } from '../../../../shared/components/svg/trash.svg';
import { DownloadSvg } from '../../../../shared/components/svg/download.svg';
import { Files } from '../../../../core/config/files-allow.config';

@Component({
  selector: 'app-details-legal-guardian',
  imports: [
    ReactiveFormsModule,
    NoEmojiDirective,
    NoSpecialCharacteresDirective,
    PhoneOnlyDirective,
    NumbersOnlyDirective,
    ArrowLeftSvg,
    PhotoSvg,
    SpinnerToButton,
    WhatsappSvg,
    UploadSvg,
    FileSvg,
    TrashSvg,
    DownloadSvg
  ],
  templateUrl: './details-legal-guardian.html',
  styleUrl: './details-legal-guardian.sass',
})
export class DetailsLegalGuardian implements OnInit {
  legalGuardianId!: string;
  editForm!: FormGroup;
  passwordForm!: FormGroup;

  private initialValues: any;

  isLoading = true;
  isSubmitting = false;
  isChangingPassword = false;
  showPassword = false;
  isDeleting = false;
  isFilesLoading = true;
  isUploadingFiles = false;

  currentPhone = '';

  selectedFiles: File[] = [];
  uploadedFiles: any[] = [];
  maxFiles: number = 5;
  maxSizeInBytes = 5 * 1024 * 1024; // 5MB

  showDeleteModal = false;

  constructor(
    private fb: FormBuilder,
    private legalGuardianApi: LegalGuardianApi,
    private router: Router,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.legalGuardianId = history.state.id || '';

    if (!this.legalGuardianId) {
      this.goBack();
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, tente novamente mais tarde'
      });
      return;
    }

    this.initForm();
    this.loadLegalGuardian();
    this.loadFiles();
  }

  get isFormChanged(): boolean {
    if (!this.initialValues) return false;
    return JSON.stringify(this.editForm.getRawValue()) !== JSON.stringify(this.initialValues);
  }

  initForm(): void {
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
      degreeOfKinship: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(30)
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

  loadLegalGuardian(): void {
    this.legalGuardianApi.findById(this.legalGuardianId)
      .subscribe({
        next: (data: LegalGuardianDetailResponseDto) => {
          this.currentPhone = data.phoneNumber;
          this.editForm.patchValue({
            username: data.username,
            email: data.email,
            cpf: data.cpf,
            phoneNumber: data.phoneNumber,
            address: data.address,
            isActive: data.isActive,
            degreeOfKinship: data.degreeOfKinship
          });
          this.editForm.markAsPristine();
          this.editForm.markAsUntouched();
          this.initialValues = this.editForm.getRawValue();
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: 'Erro ao carregar responsável.'
          });
          this.goBack();
        }
      });
  }

  loadFiles(): void {
    this.isFilesLoading = true;

    // MOCK: Simulando chamada na API
    setTimeout(() => {
      this.isFilesLoading = false;
      this.cdr.detectChanges();
    }, 1500);
  }

  openWhatsApp(): void {
    if (!this.currentPhone) {
      this.notificationService.notify({
        type: 'error',
        text: 'É necessário possui o número de telefone'
      });
      return;
    };
    const numericPhone = this.currentPhone.replace(/\D/g, '');
    const waNumber = numericPhone.startsWith('55') ? numericPhone : `55${numericPhone}`;
    window.open(`https://wa.me/${waNumber}`, '_blank');
  }

  isFieldInvalid(form: FormGroup, field: string): boolean {
    const control = form.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  goBack(): void {
    this.router.navigate(['/app/legal-guardians']);
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

    const payload: UpdateLegalGuardianRequestDto = {
      username: formValue.username.trim(),
      email: formValue.email.trim(),
      phoneNumber: formValue.phoneNumber.trim(),
      address: formValue.address.trim(),
      isActive: formValue.isActive,
      degreeOfKinship: formValue.degreeOfKinship.trim()
    }

    this.legalGuardianApi.update(this.legalGuardianId, payload)
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

    this.legalGuardianApi.changePassword(this.legalGuardianId, payload)
      .pipe(
        timeout(10000),
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

  onDeleteLegalGuardian(): void {
    this.isDeleting = true;

    this.legalGuardianApi.deleteById(this.legalGuardianId)
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
            text: 'Responsável excluído com sucesso!'
          });
          this.router.navigate(['/app/legal-guardians']);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao excluir o responsável.'
          });
        }
      });
  }

  onDownloadFile(file: any): void {
    // MOCK: Simulação de download.
    this.notificationService.notify({
      type: 'success',
      text: `Iniciando download de ${file.name}...`
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.handleFiles(input.files);
    }
    input.value = ''; // Reseta o input
  }

  onUploadFiles(): void {
    if (this.selectedFiles.length === 0) return;
    this.isUploadingFiles = true;

    // MOCK: Simulando o upload
    setTimeout(() => {
      const newSavedFiles = this.selectedFiles.map(file => ({
        id: Math.random().toString(36).substring(7),
        name: file.name,
        size: file.size,
        url: '#' // URL fake gerada pelo back-end
      }));

      this.uploadedFiles = [...this.uploadedFiles, ...newSavedFiles];
      this.selectedFiles = []; // Limpa fila
      this.isUploadingFiles = false;
      this.notificationService.notify({
        type: 'success',
        text: 'Arquivos enviados com sucesso!'
      });
      this.cdr.detectChanges();
    }, 2000);
  }

  onDeleteUploadedFile(fileId: string): void {
    const file = this.uploadedFiles.find(f => f.id === fileId);

    if (!file) return;

    file.isDeleting = true;

    // MOCK: Simulando exclusão na API
    setTimeout(() => {
      this.uploadedFiles = this.uploadedFiles.filter(f => f.id !== fileId);
      this.notificationService.notify({
        type: 'success',
        text: 'Arquivo removido com sucesso.'
      });
      this.cdr.detectChanges();
    }, 1000);
  }

  removeSelectedFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  handleFiles(files: FileList): void {
    for (let i = 0; i < files.length; i++) {
      const file = files[i];

      const fileArray = file.name.split('.');
      const extension = fileArray[fileArray.length - 1];

      if (!Files.allow.includes(extension)) {
        this.notificationService.notify({
          type: 'error',
          text: `Extensão de arquivo .${extension.toUpperCase()} não é permitida.`
        });
        continue;
      }

      if (this.selectedFiles.length + this.uploadedFiles.length >= this.maxFiles) {
        this.notificationService.notify({
          type: 'error',
          text: `Limite máximo de ${this.maxFiles} arquivos atingido.`
        });
        break;
      }

      if (file.size > this.maxSizeInBytes) {
        this.notificationService.notify({
          type: 'error',
          text: `O arquivo selecionado é muito grande. Máximo permitido: 5MB.`
        });
        continue;
      }

      this.selectedFiles.push(file);
    }
  }

  formatBytes(bytes: number, decimals = 2): string {
    if (!+bytes) return '0 Bytes';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
  }
}
