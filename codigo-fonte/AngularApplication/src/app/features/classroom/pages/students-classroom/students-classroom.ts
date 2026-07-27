import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ClassroomApi } from '../../api/classroom-api';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { Router } from '@angular/router';
import { ClassroomViewSimpleStudentResponseDto } from '../../dto/classroom-view-simple-student-response.dto';
import { StudentApi } from '../../../student/api/student-api';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { PlusSvg } from '../../../../shared/components/svg/plus.svg';
import { TrashSvg } from '../../../../shared/components/svg/trash.svg';
import { FormsModule } from '@angular/forms';
import { StudentResponseDto } from '../../../student/dto/student-response.dto';
import { catchError, finalize, Subject, throwError, timeout } from 'rxjs';
import { ClassroomDetailResponseDto } from '../../dto/classroom-detail-response.dto';
import { PageResponse } from '../../../../shared/models/page-response.model';

@Component({
  selector: 'app-students-classroom',
  imports: [
    SpinnerToButton,
    PlusSvg,
    TrashSvg,
    FormsModule
  ],
  templateUrl: './students-classroom.html',
  styleUrl: './students-classroom.sass',
})
export class StudentsClassroom implements OnInit {
  students: ClassroomViewSimpleStudentResponseDto[] = [];
  classroomId!: string;

  isLoading = true;
  removingId: string | null = null;

  isModalOpen = false;
  isSearching = false;
  availableStudents: StudentResponseDto[] = [];
  addingId: string | null = null;

  currentPage = 0;
  pageSize = 20;
  hasMore = true;
  isLoadingMore = false;

  constructor(
    private classroomApi: ClassroomApi,
    private studentApi: StudentApi,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.classroomId = history.state.id || '';
    if (!this.classroomId) {
      window.history.back();
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, tente novamente mais tarde'
      });
      return;
    }
    this.loadStudentByClassroom();
  }

  loadStudentByClassroom(): void {
    this.isLoading = true;
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
          this.students = res.students;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar os estudantes. Por favor, tente novamente mais tarde.'
          });
        }
      });
  }

  removeStudent(studentId: string): void {
    if (this.removingId) return;
    this.removingId = studentId;

    this.classroomApi.removeStudent(this.classroomId, { studentId })
      .pipe(
        finalize(() => {
          this.removingId = null;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.students = this.students.filter(s => s.id !== studentId);
          this.notificationService.notify({
            type: 'success',
            text: 'Estudante removido da turma com sucesso!'
          });
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao remover estudante.'
          });
        }
      });
  }

  goToStudent(studentId: string): void {
    this.router.navigate(
      ['/app/details-student'], 
      { state: { id: studentId } }
    );
  }

  openModal(): void {
    this.isModalOpen = true;
    this.availableStudents = [];
    this.currentPage = 0;
    this.hasMore = true;
    this.loadAvailableStudents();
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.availableStudents = [];
  }

  loadAvailableStudents(): void {
    this.isSearching = true;
    this.cdr.detectChanges();
    this.studentApi.findAll("", this.currentPage, this.pageSize)
      .pipe(
        timeout(10000),
        finalize(() => {
          this.isSearching = false;
          this.isLoadingMore = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: PageResponse<StudentResponseDto>) => {
          this.availableStudents = this.currentPage === 0
            ? res.content
            : [...this.availableStudents, ...res.content];
          this.hasMore = res.content.length === this.pageSize;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao buscar estudantes.'
          });
        }
      });
  }

  onScroll(event: Event): void {
    const target = event.target as HTMLElement;
    const isBottom = Math.abs(target.scrollHeight - target.scrollTop - target.clientHeight) <= 10;
    if (isBottom && !this.isLoadingMore && this.hasMore && !this.isSearching) {
      this.isLoadingMore = true;
      this.currentPage++;
      this.loadAvailableStudents();
    }
  }

  isAlreadyInClassroom(studentId: string): boolean {
    return this.students.some(s => s.id === studentId);
  }

  addStudent(student: StudentResponseDto): void {
    if (this.addingId || this.isAlreadyInClassroom(student.id)) return;
    this.addingId = student.id;

    this.classroomApi.addStudent(this.classroomId, { studentId: student.id })
      .pipe(
        finalize(() => {
          this.addingId = null;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.students = [...this.students, { id: student.id, name: student.name }];
          this.notificationService.notify({
            type: 'success',
            text: 'Estudante adicionado à turma com sucesso!'
          });
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao adicionar estudante.'
          });
        }
      });
  }
}
