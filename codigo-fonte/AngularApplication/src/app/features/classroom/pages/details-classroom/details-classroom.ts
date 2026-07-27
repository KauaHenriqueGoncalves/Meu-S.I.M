import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngleDownSvg } from '../../../../shared/components/svg/angle-down.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { ClassroomDetailResponseDto } from '../../dto/classroom-detail-response.dto';
import { ClassTypeResponseDto } from '../../../classtype/dto/class-type-response.dto';
import { SubjectResponseDto } from '../../../subject/dto/subject-response.dto';
import { ClassroomApi } from '../../api/classroom-api';
import { ClassTypeApi } from '../../../classtype/api/class-type-api';
import { SubjectApi } from '../../../subject/api/subject-api';
import { ClassTypeService } from '../../../classtype/service/class-type.service';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { UpdateClassroomRequestDto } from '../../dto/update-classroom-resquest.dto';

@Component({
  selector: 'app-details-classroom',
  imports: [
    ReactiveFormsModule,
    AngleDownSvg,
    SpinnerToButton,
    NoEmojiDirective
  ],
  templateUrl: './details-classroom.html',
  styleUrl: './details-classroom.sass',
})
export class DetailsClassroom implements OnInit {
  classroomId!: string;
  classroomForm!: FormGroup;

  detail?: ClassroomDetailResponseDto;

  classTypes: ClassTypeResponseDto[] = [];
  subjects: SubjectResponseDto[] = [];

  isSubjectDropdownOpen = false;
  selectedSubjectName = '';

  subjectPage = 0;
  subjectSize = 60;
  isLoadingMore = false;
  hasMoreSubjects = true;
  isAppending = false;

  isLoading = true;
  isSubmitting = false;

  isDeleteModalOpen = false;

  private initialValues: any;

  constructor(
    private fb: FormBuilder,
    private classroomApi: ClassroomApi,
    private classTypeApi: ClassTypeApi,
    private subjectApi: SubjectApi,
    private classTypeService: ClassTypeService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.classroomId = history.state.id || '';
    if (!this.classroomId) {
      this.goBack();
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, tente novamente mais tarde'
      });
      return;
    }
    this.initForm();
    this.loadClassTypes();
    this.loadSubjects();
    this.loadClassroom();
  }

  get isFormChanged(): boolean {
    if (!this.initialValues) return false;
    return JSON.stringify(this.classroomForm.getRawValue()) !== JSON.stringify(this.initialValues);
  }

  getFriendlyClassTypeName(dbName: string): string {
    return this.classTypeService.getFriendlyClassTypeName(dbName);
  }

  goBack(): void {
    this.router.navigate(['/app/classrooms']);
  }

  initForm(): void {
    this.classroomForm = this.fb.group({
      name: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(60)
      ]],
      description: ['', [
        Validators.maxLength(200)
      ]],
      maxStudents: [30, [
        Validators.required,
        Validators.min(1),
        Validators.max(200)
      ]],
      classTypeId: [null,
        Validators.required
      ],
      subjectId: [null,
        Validators.required
      ]
    });

    this.classroomForm.get('classTypeId')?.valueChanges.subscribe(typeId => {
      const maxStudentsControl = this.classroomForm.get('maxStudents');
      if (typeId == 1) {
        maxStudentsControl?.setValue(1);
        maxStudentsControl?.disable();
      }
      else {
        maxStudentsControl?.enable();
      }
    });
  }

  isFieldInvalid(group: FormGroup, field: string): boolean {
    const control = group.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  loadClassTypes(): void {
    this.classTypeApi.findAll()
      .subscribe({
        next: (res: ClassTypeResponseDto[]) => {
          this.classTypes = res;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar tipos de turma'
          });
        }
      });
  }

  loadSubjects(): void {
    if (!this.isAppending) {
      this.subjectPage = 0;
    }
    this.subjectApi.findAll(this.subjectPage, this.subjectSize)
      .subscribe({
        next: (res: PageResponse<SubjectResponseDto>) => {
          if (this.isAppending) {
            this.subjects = [...this.subjects, ...res.content];
          } else {
            this.subjects = res.content;
          }
          this.hasMoreSubjects = res.content.length === this.subjectSize;
          this.isAppending = false;
          this.isLoadingMore = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar disciplinas'
          });
          this.isLoadingMore = false;
          this.isAppending = false;
          this.cdr.detectChanges();
        }
      });
  }

  loadClassroom(): void {
    this.classroomApi.findById(this.classroomId)
      .pipe(
        timeout(10000),
        catchError((error) => throwError(() => error)),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: ClassroomDetailResponseDto) => {
          this.detail = res;
          this.selectedSubjectName = res.subject?.name || '';
          this.classroomForm.patchValue({
            name: res.name,
            description: res.description,
            maxStudents: res.maxStudents,
            classTypeId: res.classType?.id,
            subjectId: res.subject?.id
          });
          this.classroomForm.markAsPristine();
          this.classroomForm.markAsUntouched();
          this.initialValues = this.classroomForm.getRawValue();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar turma. Por favor, tente novamente mais tarde.'
          });
          this.goBack();
        }
      });
  }

  selectSubject(subject: SubjectResponseDto): void {
    this.classroomForm.patchValue({ subjectId: subject.id });
    this.selectedSubjectName = subject.name;
    this.isSubjectDropdownOpen = false;
  }

  toggleSubjectDropdown(): void {
    this.isSubjectDropdownOpen = !this.isSubjectDropdownOpen;
  }

  onScroll(event: Event): void {
    const target = event.target as HTMLElement;
    const isBottom = Math.abs(target.scrollHeight - target.scrollTop - target.clientHeight) <= 10;
    if (isBottom && !this.isLoadingMore && this.hasMoreSubjects) {
      this.isLoadingMore = true;
      this.isAppending = true;
      this.subjectPage++;
      this.loadSubjects();
    }
  }

  onSubmit(): void {
    if (this.classroomForm.invalid) {
      this.classroomForm.markAllAsTouched();
      this.notificationService.notify({
        type: 'warning',
        text: 'Por favor, preencha todos os campos obrigatórios corretamente.'
      });
      return;
    }

    this.isSubmitting = true;

    const formValue = this.classroomForm.getRawValue();

    const payload: UpdateClassroomRequestDto = {
      classTypeId: formValue.classTypeId,
      subjectId: formValue.subjectId,
      maxStudents: formValue.maxStudents,
      name: formValue.name.trim(),
      description: formValue.description?.trim() || ''
    };

    this.classroomApi.update(this.classroomId, payload)
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
          this.classroomForm.markAsPristine();
          this.classroomForm.markAsUntouched();
          this.initialValues = this.classroomForm.getRawValue();
          this.notificationService.notify({
            type: 'success',
            text: 'Turma atualizada com sucesso!'
          });
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao atualizar turma. Tente novamente.'
          });
        }
      });
  }

  closeDeleteModal(): void {
    if (this.isSubmitting) return;
    this.isDeleteModalOpen = false;
  }

  confirmDelete(): void {
    this.isSubmitting = true;
    this.cdr.detectChanges();
    this.classroomApi.deleteById(this.classroomId)
      .pipe(
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.notificationService.notify({
            type: 'success',
            text: 'Turma excluida com sucesso!'
          });
          this.router.navigate(['/app/classrooms']);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao excluir a turma.'
          });
        }
      });
  }
}
