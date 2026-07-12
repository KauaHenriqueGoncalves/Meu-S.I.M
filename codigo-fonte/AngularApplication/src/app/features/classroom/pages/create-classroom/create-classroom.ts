import { ChangeDetectorRef, Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SubjectResponseDto } from '../../../subject/dto/subject-response.dto';
import { ClassTypeResponseDto } from '../../../classtype/dto/class-type-response.dto';
import { ClassroomApi } from '../../api/classroom-api';
import { ClassTypeApi } from '../../../classtype/api/class-type-api';
import { SubjectApi } from '../../../subject/api/subject-api';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { CreateClassroomRequestDto } from '../../dto/create-classroom-resquest.dto';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { AngleDownSvg } from '../../../../shared/components/svg/angle-down.svg';
import { ClassTypeService } from '../../../classtype/service/class-type.service';

@Component({
  selector: 'app-create-classroom',
  imports: [
    ReactiveFormsModule,
    ArrowLeftSvg,
    AngleDownSvg,
    SpinnerToButton,
    NoEmojiDirective,
  ],
  templateUrl: './create-classroom.html',
  styleUrl: './create-classroom.sass',
})
export class CreateClassroom implements OnInit {
  classroomForm!: FormGroup;

  classTypes: ClassTypeResponseDto[] = [];
  subjects: SubjectResponseDto[] = [];

  isSubjectDropdownOpen = false;
  selectedSubjectName = '';

  subjectPage = 0;
  subjectSize = 40;
  isLoading = false;
  isSubmitting = false;

  isLoadingMore = false;
  hasMoreSubjects = true;
  isAppending = false;

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
    this.initForm();
    this.setupClassTypeListener();
    this.loadClassTypes();
    this.loadSubjects();
  }

  getFriendlyClassTypeName(dbName: string): string {
    return this.classTypeService.getFriendlyClassTypeName(dbName);
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
  }

  goBack(): void {
    this.router.navigate(['/app/classrooms']);
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
      this.isLoading = true;
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
          this.isLoading = false;
          this.isLoadingMore = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar disciplinas'
          });
          this.isLoading = false;
          this.isLoadingMore = false;
          this.isAppending = false;
          this.cdr.detectChanges();
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

  setupClassTypeListener(): void {
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

    const payload: CreateClassroomRequestDto = {
      classTypeId: formValue.classTypeId,
      subjectId: formValue.subjectId,
      maxStudents: formValue.maxStudents,
      name: formValue.name.trim(),
      description: formValue.description.trim()
    };

    this.classroomApi.create(payload)
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
            text: 'Turma criada com sucesso'
          });
          this.router.navigate(['/app/classrooms']);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao criar turma. Tente novamente.'
          });
        }
      });
  }
}
