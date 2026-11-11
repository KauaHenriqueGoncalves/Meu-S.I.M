import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DatePipe, NgClass } from '@angular/common';
import { Router } from '@angular/router';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { LessonApi } from '../../../lesson/api/lesson-api';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { ScheduleService } from '../../../schedule/service/schedule.service';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { LessonSimpleViewResponseDTO } from '../../../lesson/dto/lesson-simples-view-response.dto';
import { LessonStatus } from '../../../lesson/enum/lesson-status.enum';

@Component({
  selector: 'app-lessons-classroom',
  imports: [
    DatePipe,
    NgClass,
    SpinnerToButton
  ],
  templateUrl: './lessons-classroom.html',
  styleUrl: './lessons-classroom.sass',
})
export class LessonsClassroom implements OnInit {
  String(arg0: LessonStatus) {
    throw new Error('Method not implemented.');
  }
  classroomId!: string;
  lessons: LessonSimpleViewResponseDTO[] = [];

  isLoading = true;
  isPaginating = false;
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(
    private lessonApi: LessonApi,
    private scheduleService: ScheduleService,
    private notificationService: NotificationService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.classroomId = history.state.id || '';
    if (!this.classroomId) {
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, tente novamente mais tarde'
      });
      return;
    }
    this.loadLessons();
  }

  getWeekdayName(weekday: number): string {
    return this.scheduleService.getFriendlyWeekday(ScheduleService.weeks[weekday - 1]);
  }

  getStatusLabel(status: any): string {
    switch (String(status)) {
      case 'SCHEDULED': return 'Agendada';
      case 'COMPLETED': return 'Concluída';
      case 'CANCELED': return 'Cancelada';
      default: return String(status);
    }
  }

  statusClass(status: any): string {
    return String(status).toLowerCase();
  }

  loadLessons(): void {
    this.isPaginating = true;
    this.lessonApi.page(this.classroomId, this.currentPage, this.pageSize)
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
        next: (res: PageResponse<LessonSimpleViewResponseDTO>) => {
          this.lessons = res.content;
          this.totalPages = res.totalPages;
          this.totalElements = res.totalElements;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar as agendas.'
          });
        }
      });
  }

  goToDetails(lessonId: string): void {
    this.router.navigate(
      ['/app/about-classroom/lesson-details'],
      { state: { id: this.classroomId, lessonId } }
    );
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages && !this.isPaginating) {
      this.currentPage = page;
      this.loadLessons();
    }
  }
}
