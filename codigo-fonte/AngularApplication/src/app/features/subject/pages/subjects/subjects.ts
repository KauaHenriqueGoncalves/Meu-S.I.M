import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SubjectResponseDto } from '../../dto/subject-response.dto';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SubjectApi } from '../../api/subject-api';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { SubjectRequestDto } from '../../dto/subject-request.dto';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { PlusSvg } from '../../../../shared/components/svg/plus.svg';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { NoSpecialCharacteresDirective } from '../../../../shared/directives/no-special-characteres.directive';
import { TrashSvg } from "../../../../shared/components/svg/trash.svg";
import { EditSvg } from '../../../../shared/components/svg/edit.svg';

@Component({
  selector: 'app-subjects',
  imports: [
    ReactiveFormsModule,
    SpinnerToButton,
    PlusSvg,
    NoEmojiDirective,
    TrashSvg,
    EditSvg
  ],
  templateUrl: './subjects.html',
  styleUrl: './subjects.sass',
})
export class Subjects implements OnInit {
  subjects: SubjectResponseDto[] = [];
  isLoading = true;
  isSubmitting = false;

  currentPage = 0;
  pageSize = 60;
  totalPages = 0;
  totalElements = 0;
  isPaginating = false;

  isModalOpen = false;
  isDeleteModalOpen = false;

  private formValues: any;

  subjectForm!: FormGroup;
  editingId: string | null = null;
  deletingId: string | null = null;

  constructor(
    private subjectApi: SubjectApi,
    private fb: FormBuilder,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.loadSubjects();
  }

  get isFormChanged(): boolean {
    if (!this.formValues) return false;
    return JSON.stringify(this.subjectForm.getRawValue()) !== JSON.stringify(this.formValues);
  }

  initForm(): void {
    this.subjectForm = this.fb.group({
      name: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50)
      ]]
    });
  }

  isFieldInvalid(field: string): boolean {
    const control = this.subjectForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  loadSubjects(): void {
    this.isLoading = true;
    this.isPaginating = true;
    this.cdr.detectChanges();

    this.subjectApi.findAll(this.currentPage, this.pageSize)
      .pipe(
        timeout(10000),
        catchError((error) => throwError(() => error)),
        finalize(() => {
          this.isLoading = false;
          this.isPaginating = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response: PageResponse<SubjectResponseDto>) => {
          this.subjects = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro inesperado ao carregar disciplinas.'
          });
        }
      });
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadSubjects();
    }
  }

  openModal(subject?: SubjectResponseDto): void {
    if (subject) {
      this.editingId = subject.id;
      this.subjectForm.patchValue({ name: subject.name });
    }
    else {
      this.editingId = null;
      this.subjectForm.reset();
    }
    this.formValues = this.subjectForm.getRawValue();
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.editingId = null;
    this.subjectForm.reset();
    this.formValues = null;
  }

  onSaveSubject(): void {
    if (this.subjectForm.invalid) {
      this.subjectForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const requestData: SubjectRequestDto = {
      name: this.subjectForm.value.name.trim()
    };

    const request$ = this.editingId
      ? this.subjectApi.update(this.editingId, requestData)
      : this.subjectApi.create(requestData);

    request$.pipe(
      timeout(10000),
      finalize(() => {
        this.isSubmitting = false;
        this.formValues = null;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: () => {
        this.closeModal();
        this.loadSubjects();
        this.notificationService.notify({
          type: 'success',
          text: this.editingId ? 'Disciplina atualizada com sucesso!' : 'Disciplina criada com sucesso.'
        });
      },
      error: (err) => {
        this.notificationService.notify({
          type: 'error',
          text: err.error?.message || 'Erro inesperado.'
        });
      }
    });
  }

  openDeleteModal(id: string): void {
    this.deletingId = id;
    this.isDeleteModalOpen = true;
  }

  closeDeleteModal(): void {
    this.isDeleteModalOpen = false;
    this.deletingId = null;
  }

  confirmDelete(): void {
    if (!this.deletingId) return;

    this.isSubmitting = true;
    this.subjectApi.deleteById(this.deletingId)
      .pipe(
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.closeDeleteModal();
          this.loadSubjects();
          this.notificationService.notify({
            type: 'success',
            text: 'Disciplina excluída com sucesso.'
          });
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao excluir disciplina.'
          });
        }
      });
  }
}
