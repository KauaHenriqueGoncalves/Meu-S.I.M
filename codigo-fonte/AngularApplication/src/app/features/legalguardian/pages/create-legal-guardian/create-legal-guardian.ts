import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LegalGuardianApi } from '../../api/legal-guardian-api';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { cpfValidator } from '../../../../shared/validation/cpf.validator';
import { CreateLegalGuardianRequestDto } from '../../dto/create-legal-guardian-request.dto';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { PhotoSvg } from '../../../../shared/components/svg/photo.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { NoSpecialCharacteresDirective } from '../../../../shared/directives/no-special-characteres.directive';
import { NumbersOnlyDirective } from '../../../../shared/directives/numbers-only.directive';
import { PhoneOnlyDirective } from '../../../../shared/directives/phone-only.directive';

@Component({
  selector: 'app-create-legal-guardian',
  imports: [
    ReactiveFormsModule,
    ArrowLeftSvg,
    PhotoSvg,
    SpinnerToButton,
    NoEmojiDirective,
    NoSpecialCharacteresDirective,
    NumbersOnlyDirective,
    PhoneOnlyDirective
  ],
  templateUrl: './create-legal-guardian.html',
  styleUrl: './create-legal-guardian.sass',
})
export class CreateLegalGuardian implements OnInit {
  @ViewChild('container') container!: ElementRef;

  legalGuardianForm!: FormGroup;
  isSubmitting = false;

  showPassword = false;

  selectedFiles: File[] = [];
  maxFiles: number = 5;

  constructor(
    private fb: FormBuilder,
    private legalGuardianApi: LegalGuardianApi,
    private router: Router,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.initForm();
  }

  get userForm(): FormGroup {
    return this.legalGuardianForm.get('userRequest') as FormGroup;
  }

  get lgForm(): FormGroup {
    return this.legalGuardianForm.get('legalGuardianRequest') as FormGroup;
  }

  initForm(): void {
    this.legalGuardianForm = this.fb.group({
      userRequest: this.fb.group({
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
      }),
      legalGuardianRequest: this.fb.group({
        degreeOfKinship: ['', [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(30)
        ]]
      })
    });
  }

  goBack(): void {
    this.router.navigate(['/app/legal-guardians']);
  }

  isFieldInvalid(group: FormGroup, field: string): boolean {
    const control = group.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onFileSelected(event: Event): void {
    const element = event.currentTarget as HTMLInputElement;
    const files: FileList | null = element.files;

    if (!files) return;

    const maxSizeInBytes = 5 * 1024 * 1024; // 5MB

    for (let i = 0; i < files.length; i++) {
      const file = files[i];

      const fileSizeInBytes: number = file.size;

      if (fileSizeInBytes > maxSizeInBytes) {
        this.notificationService.notify({
          type: 'error',
          text: `O arquivo selecionado é muito grande. Máximo permitido: 5MB.`
        });
        continue;
      }

      if (this.selectedFiles?.length >= this.maxFiles) {
        this.notificationService.notify({
          type: 'error',
          text: 'Apenas é suportado o total de 5 arquivos.'
        });
        return;
      }

      this.selectedFiles.push(files[i]);
    }

    if (element) {
      element.value = '';
    }

    this.moveScroolToDown(this.container);
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  formatBytes(bytes: number, decimals = 2): string {
    if (!+bytes) return '0 Bytes';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
  }

  moveScroolToDown(container: ElementRef): void {
    setTimeout(() => {
      let el: HTMLElement | null = container.nativeElement.parentElement;

      while (el) {
        if (el.scrollHeight > el.clientHeight) {
          el.scrollTo({
            top: el.scrollHeight,
            behavior: 'smooth'
          });
          break;
        }
        el = el.parentElement;
      }
    }, 0);
  }

  onSubmit(): void {
    if (this.legalGuardianForm.invalid) {
      this.legalGuardianForm.markAllAsTouched();
      this.notificationService.notify({
        type: 'warning',
        text: 'Por favor, preencha todos os campos obrigatórios corretamente.'
      });
      return;
    }

    this.isSubmitting = true;

    const formValue = this.legalGuardianForm.value;

    const payload: CreateLegalGuardianRequestDto = {
      userRequest: {
        username: formValue.userRequest.username?.trim(),
        email: formValue.userRequest.email?.trim(),
        password: formValue.userRequest.password?.trim(),
        cpf: formValue.userRequest.cpf?.trim(),
        phoneNumber: formValue.userRequest.phoneNumber?.trim(),
        address: formValue.userRequest.address?.trim()
      },
      legalGuardianRequest: {
        degreeOfKinship: formValue.legalGuardianRequest.degreeOfKinship?.trim()
      }
    };

    this.legalGuardianApi.create(payload)
      .pipe(
        timeout(10000),
        catchError((error) => {
          return throwError(() => error);
        }),
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.notificationService.notify({
            type: 'success',
            text: 'Responsável criado com sucesso!'
          });
          this.router.navigate(['/app/legal-guardians']);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao criar responsável. Tente novamente.'
          });
        }
      });
  }
}
