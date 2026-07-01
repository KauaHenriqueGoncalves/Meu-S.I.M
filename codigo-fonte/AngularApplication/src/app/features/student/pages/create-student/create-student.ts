import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { PhotoSvg } from '../../../../shared/components/svg/photo.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { NoSpecialCharacteresDirective } from '../../../../shared/directives/no-special-characteres.directive';
import { LegalGuardianViewSimpleResponseDto } from '../../../legalguardian/dto/legal-guardian-view-simple-response.dto';
import { catchError, debounceTime, finalize, Subject, throwError, timeout } from 'rxjs';
import { StudentApi } from '../../api/student-api';
import { LegalGuardianApi } from '../../../legalguardian/api/legal-guardian-api';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { CreateStudentRequestDto } from '../../dto/create-student-request.dto';
import { Files } from '../../../../core/config/files-allow.config';
import { UploadSvg } from '../../../../shared/components/svg/upload.svg';
import { FileSvg } from '../../../../shared/components/svg/file.svg';
import { TrashSvg } from '../../../../shared/components/svg/trash.svg';

@Component({
  selector: 'app-create-student',
  imports: [
    ReactiveFormsModule,
    ArrowLeftSvg,
    PhotoSvg,
    UploadSvg,
    FileSvg,
    TrashSvg,
    SpinnerToButton,
    NoEmojiDirective,
    NoSpecialCharacteresDirective
  ],
  templateUrl: './create-student.html',
  styleUrl: './create-student.sass',
})
export class CreateStudent implements OnInit {
  @ViewChild('container') container!: ElementRef;

  studentForm!: FormGroup;
  isSubmitting = false;

  selectedFiles: File[] = [];
  maxFiles = 5;
  maxSizeInBytes = 5 * 1024 * 1024; // 5MB
  isDragging = false;

  searchGuardianSubject = new Subject<string>();
  suggestedGuardians: LegalGuardianViewSimpleResponseDto[] = [];
  selectedGuardian: LegalGuardianViewSimpleResponseDto | null = null;
  isSearchingGuardian = false;

  minDate = '1980-01-01';
  maxDate = new Date().toISOString().split('T')[0];

  constructor(
    private fb: FormBuilder,
    private studentApi: StudentApi,
    private legalGuardianApi: LegalGuardianApi,
    private router: Router,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {
    this.searchGuardianSubject.pipe(
      debounceTime(500)
    ).subscribe(term => {
      this.fetchGuardians(term);
    });
  }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.studentForm = this.fb.group({
      name: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(100)
      ]],
      dateOfBirth: ['', [
        Validators.required
      ]],
      grade: ['', [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(20)
      ]],
      legalGuardianId: ['', [
        Validators.required
      ]]
    });
  }

  goBack(): void {
    this.router.navigate(['/app/students']);
  }

  isFieldInvalid(field: string): boolean {
    const control = this.studentForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSearchGuardianInput(event: any): void {
    const term = event.target.value.trim();
    if (term.length >= 1) {
      this.isSearchingGuardian = true;
      this.searchGuardianSubject.next(term);
    }
    else {
      this.suggestedGuardians = [];
      this.isSearchingGuardian = false;
    }
  }

  fetchGuardians(name: string): void {
    this.legalGuardianApi.findAll(name, 0, 5)
      .subscribe({
        next: (res: PageResponse<LegalGuardianViewSimpleResponseDto>) => {
          this.suggestedGuardians = res.content;
          this.isSearchingGuardian = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.isSearchingGuardian = false;
          this.suggestedGuardians = [];
        }
      });
  }

  selectGuardian(guardian: LegalGuardianViewSimpleResponseDto): void {
    this.selectedGuardian = guardian;
    this.studentForm.patchValue({ legalGuardianId: guardian.id });
    this.suggestedGuardians = [];
  }

  clearGuardianSelection(): void {
    this.selectedGuardian = null;
    this.studentForm.patchValue({ legalGuardianId: '' });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.handleFiles(input.files);
    }
    input.value = ''; // Reseta o input
    this.moveScroolToDown(this.container);
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

      if (this.selectedFiles.length >= this.maxFiles) {
        this.notificationService.notify({
          type: 'error',
          text: `Limite máximo de ${this.maxFiles} arquivos atingido.`
        });
        continue;
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
    if (this.studentForm.invalid) {
      this.studentForm.markAllAsTouched();
      this.notificationService.notify({
        type: 'warning',
        text: 'Preencha todos os campos obrigatórios.'
      });
      return;
    }

    this.isSubmitting = true;
    const formValue = this.studentForm.value;

    const payload: CreateStudentRequestDto = {
      name: formValue.name.trim(),
      dateOfBirth: new Date(formValue.dateOfBirth + 'T00:00:00'),
      grade: formValue.grade.trim(),
      legalGuardianId: formValue.legalGuardianId.trim()
    };

    this.studentApi.create(payload)
      .pipe(
        timeout(10000),
        catchError(error => throwError(() => error)),
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.notificationService.notify({
            type: 'success',
            text: 'Estudante matriculado com sucesso!'
          });
          // Futuro: Lógica para enviar this.selectedFiles
          this.router.navigate(['/app/students']);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error', text:
              err.error?.message || 'Erro ao criar estudante.'
          });
        }
      });
  }
}
