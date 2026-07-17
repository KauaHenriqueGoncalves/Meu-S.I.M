import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { StudentApi } from '../../api/student-api';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { LegalGuardianViewSimpleResponseDto } from '../../../legalguardian/dto/legal-guardian-view-simple-response.dto';
import { catchError, debounceTime, finalize, Subject, throwError, timeout } from 'rxjs';
import { LegalGuardianApi } from '../../../legalguardian/api/legal-guardian-api';
import { StudentDetailRequestDto } from '../../dto/student-detail-request.dto';
import { Files } from '../../../../core/config/files-allow.config';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { PhotoSvg } from '../../../../shared/components/svg/photo.svg';
import { UploadSvg } from '../../../../shared/components/svg/upload.svg';
import { FileSvg } from '../../../../shared/components/svg/file.svg';
import { TrashSvg } from '../../../../shared/components/svg/trash.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { UpdateStudentRequestDto } from '../../dto/update-student-request.dto';
import { DownloadSvg } from '../../../../shared/components/svg/download.svg';
import { CancelSvg } from '../../../../shared/components/svg/cancel.svg';
import { ClassroomViewSimpleResponseDto } from '../../../classroom/dto/classroom-view-simple-response.dto';
import { AngleDownSvg } from '../../../../shared/components/svg/angle-down.svg';
import { ClassroomApi } from '../../../classroom/api/classroom-api';
import { BookSvg } from '../../../../shared/components/svg/book.svg';
import { ClassTypeService } from '../../../classtype/service/class-type.service';

@Component({
  selector: 'app-details-student',
  imports: [
    ReactiveFormsModule,
    SpinnerToButton,
    ArrowLeftSvg,
    PhotoSvg,
    CancelSvg,
    UploadSvg,
    FileSvg,
    DownloadSvg,
    TrashSvg,
    BookSvg,
    AngleDownSvg
  ],
  templateUrl: './details-student.html',
  styleUrl: './details-student.sass',
})
export class DetailsStudent implements OnInit {
  studentId!: string;
  editForm!: FormGroup;

  private initialValues: any;

  isLoading = true;
  isSubmitting = false;
  isDeleting = false;
  isFilesLoading = true;
  isUploadingFiles = false;

  isClassroomLoading = true;
  classrooms!: ClassroomViewSimpleResponseDto[];

  selectedFiles: File[] = [];
  uploadedFiles: any[] = [];
  maxFiles: number = 5;
  maxSizeInBytes = 5 * 1024 * 1024; // 5MB

  searchGuardianSubject = new Subject<string>();
  suggestedGuardians: LegalGuardianViewSimpleResponseDto[] = [];
  selectedGuardian: LegalGuardianViewSimpleResponseDto | null = null;
  isSearchingGuardian = false;

  minDate = '1980-01-01';
  maxDate = new Date().toISOString().split('T')[0];

  showDeleteModal = false;

  constructor(
    private fb: FormBuilder,
    private studentApi: StudentApi,
    private legalGuardianApi: LegalGuardianApi,
    private classroomApi: ClassroomApi,
    private classTypeService: ClassTypeService,
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
    this.studentId = history.state.id || '';

    if (!this.studentId) {
      this.goBack();
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, tente novamente mais tarde'
      });
      return;
    }

    this.initForm();
    this.loadStudent();
    this.loadFiles();
    this.loadClassroom();
  }

  get isFormChanged(): boolean {
    if (!this.initialValues) return false;
    return JSON.stringify(this.editForm.getRawValue()) !== JSON.stringify(this.initialValues);
  }

  getFriendlyClassTypeName(dbName: string): string {
    return this.classTypeService.getFriendlyClassTypeName(dbName);
  }

  initForm(): void {
    this.editForm = this.fb.group({
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

  loadStudent(): void {
    this.isLoading = true;
    this.cdr.detectChanges();
    this.studentApi.findById(this.studentId)
      .subscribe({
        next: (res: StudentDetailRequestDto) => {
          this.editForm.patchValue({
            name: res.name,
            dateOfBirth: res.dateOfBirth,
            grade: res.grade,
            legalGuardianId: res.legalGuardianResponse?.id || ''
          });
          this.selectedGuardian = res.legalGuardianResponse || null;
          this.editForm.markAsPristine();
          this.editForm.markAsUntouched();
          this.initialValues = this.editForm.getRawValue();
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar o estudante. Por favor, tente novamente mais tarde.'
          });
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

  loadClassroom(): void {
    this.classroomApi.findAllByStudentId(this.studentId)
      .subscribe({
        next: (res: ClassroomViewSimpleResponseDto[]) => {
          this.classrooms = res;
          this.isClassroomLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.isClassroomLoading = false;
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar o turmas. Por favor, tente novamente mais tarde.'
          });
        }
      });
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
    this.editForm.patchValue({ legalGuardianId: guardian.id });
    this.suggestedGuardians = [];
  }

  clearGuardianSelection(): void {
    this.selectedGuardian = null;
    this.editForm.patchValue({ legalGuardianId: '' });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.editForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  goBack(): void {
    window.history.back();
  }

  goToClassroom(id: string): void {
    this.router.navigate(
      ['/app/about-classroom/details'],
      { state: { id: id } }
    );
  }

  goToLegalGuardianDetails(): void {
    if (!this.selectedGuardian) {
      this.notificationService.notify({
        type: 'warning',
        text: 'Nenhum responsável legal selecionado.'
      });
      return;
    }

    this.router.navigate(['/app/details-legal-guardian'],
      { state: { id: this.selectedGuardian.id } }
    );
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

    const payload: UpdateStudentRequestDto = {
      name: formValue.name.trim(),
      dateOfBirth: new Date(formValue.dateOfBirth + 'T00:00:00'),
      grade: formValue.grade.trim(),
      legalGuardianId: formValue.legalGuardianId.trim()
    };

    this.studentApi.update(this.studentId, payload)
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
            text: err.error?.message || 'Erro ao atualizar o estudante. Por favor, tente novamente mais tarde.'
          });
        }
      });
  }

  onDeleteStudent(): void {
    this.isDeleting = true;
    this.studentApi.deleteById(this.studentId)
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
            text: 'Estudante excluído com sucesso!'
          });
          this.router.navigate(['/app/students']);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao excluir o estudante. Por favor, tente novamente mais tarde.'
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

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.handleFiles(input.files);
    }
    input.value = ''; // Reseta o input
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
