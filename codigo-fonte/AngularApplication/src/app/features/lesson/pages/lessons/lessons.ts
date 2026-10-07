import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AgendaDayResponseDto } from '../../dto/agenda-day-response.dto';
import { FormsModule } from '@angular/forms';
import { AngleDownSvg } from '../../../../shared/components/svg/angle-down.svg';
import { ClassroomViewSimpleResponseDto } from '../../../classroom/dto/classroom-view-simple-response.dto';
import { LessonDisplayStatus } from '../../enum/lesson-display-status.enum';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { ClassroomApi } from '../../../classroom/api/classroom-api';
import { ScheduleService } from '../../../schedule/service/schedule.service';
import { LessonApi } from '../../api/lesson-api';
import { timeout, catchError, throwError, finalize } from 'rxjs';
import { PageResponse } from '../../../../shared/models/page-response.model';

interface CalendarCell {
  date: Date;
  inMonth: boolean;
  isToday: boolean;
  events: AgendaDayResponseDto[];
}

@Component({
  selector: 'app-lessons',
  imports: [
    FormsModule,
    AngleDownSvg
  ],
  templateUrl: './lessons.html',
  styleUrl: './lessons.sass',
})
export class Lessons implements OnInit {
  readonly MAX_VISIBLE = 3;
  
  readonly weekLabels = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
  
  readonly monthNames = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];
  
  readonly Status = LessonDisplayStatus;

  classrooms: ClassroomViewSimpleResponseDto[] = [];
  classroomId = '';

  year = new Date().getFullYear();
  month = new Date().getMonth() + 1;

  cells: CalendarCell[] = [];
  agenda: AgendaDayResponseDto[] = [];
  summary: Record<LessonDisplayStatus, number> = this.emptySummary();

  isLoadingClassrooms = true;
  isLoadingAgenda = false;

  selectedCell: CalendarCell | null = null;

  constructor(
    private classroomApi: ClassroomApi,
    private lessonApi: LessonApi,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.buildCalendar();
    this.loadClassrooms();
  }

  get monthLabel(): string {
    return `${this.monthNames[this.month - 1]} ${this.year}`;
  }

  private emptySummary(): Record<LessonDisplayStatus, number> {
    return {
      [LessonDisplayStatus.DONE]: 0,
      [LessonDisplayStatus.PENDING]: 0,
      [LessonDisplayStatus.LATE]: 0,
      [LessonDisplayStatus.CANCELED]: 0,
    };
  }

  loadClassrooms(): void {
    this.classroomApi.findAll(0, 200)
      .pipe(
        timeout(10000),
        catchError((e) => throwError(() => e)),
        finalize(() => {
          this.isLoadingClassrooms = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: PageResponse<ClassroomViewSimpleResponseDto>) => {
          this.classrooms = res.content;
          if (this.classrooms.length === 1) {
            this.classroomId = this.classrooms[0].id;
            this.loadAgenda();
          }
        },
        error: (err) => this.notificationService.notify({
          type: 'error',
          text: err.error?.message || 'Erro ao carregar turmas.'
        })
      });
  }

  onClassroomChange(): void {
    this.loadAgenda();
  }

  loadAgenda(): void {
    this.selectedCell = null;
    if (!this.classroomId) {
      this.agenda = [];
      this.buildCalendar();
      return;
    }
    this.isLoadingAgenda = true;
    this.cdr.detectChanges();
    this.lessonApi.getAgendaDays(this.classroomId, this.year, this.month)
      .pipe(
        timeout(10000),
        catchError((e) => throwError(() => e)),
        finalize(() => {
          this.isLoadingAgenda = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: AgendaDayResponseDto[]) => {
          this.agenda = res;
          this.buildCalendar();
        },
        error: (err) => this.notificationService.notify({
          type: 'error',
          text: err.error?.message || 'Erro ao carregar agenda.'
        })
      });
  }

  prevMonth(): void {
    this.month--;
    if (this.month < 1) { this.month = 12; this.year--; }
    this.changePeriod();
  }

  nextMonth(): void {
    this.month++;
    if (this.month > 12) { this.month = 1; this.year++; }
    this.changePeriod();
  }

  goToday(): void {
    const now = new Date();
    this.year = now.getFullYear();
    this.month = now.getMonth() + 1;
    this.changePeriod();
  }

  private changePeriod(): void {
    this.agenda = [];
    this.buildCalendar();
    this.loadAgenda();
  }

  private key(d: Date): string {
    return `${d.getFullYear()}-${d.getMonth()}-${d.getDate()}`;
  }

  private parseDate(value: string): Date {
    const [y, m, d] = value.substring(0, 10).split('-').map(Number);
    return new Date(y, m - 1, d);
  }

  private buildCalendar(): void {
    const byDay = new Map<string, AgendaDayResponseDto[]>();
    const summary = this.emptySummary();

    for (const a of this.agenda) {
      const k = this.key(this.parseDate(a.date));
      byDay.set(k, [...(byDay.get(k) ?? []), a]);
      summary[a.displayStatus]++;
    }
    this.summary = summary;

    const first = new Date(this.year, this.month - 1, 1);
    const start = new Date(first);
    start.setDate(1 - first.getDay());

    const todayKey = this.key(new Date());
    const cells: CalendarCell[] = [];

    for (let i = 0; i < 42; i++) {
      const d = new Date(start);
      d.setDate(start.getDate() + i);
      cells.push({
        date: d,
        inMonth: d.getMonth() === this.month - 1,
        isToday: this.key(d) === todayKey,
        events: (byDay.get(this.key(d)) ?? [])
          .sort((a, b) => a.startTime.localeCompare(b.startTime))
      });
    }

    // remove a última semana se estiver totalmente fora do mês
    if (!cells.slice(35).some(c => c.inMonth)) cells.length = 35;

    this.cells = cells;
    this.cdr.detectChanges();
  }

  statusLabel(s: LessonDisplayStatus): string {
    switch (s) {
      case LessonDisplayStatus.DONE: return 'Realizada';
      case LessonDisplayStatus.CANCELED: return 'Cancelada';
      case LessonDisplayStatus.LATE: return 'Atrasada';
      default: return 'Pendente';
    }
  }

  time(e: AgendaDayResponseDto): string {
    return `${e.startTime.substring(0, 5)} - ${e.endTime.substring(0, 5)}`;
  }

  openDay(cell: CalendarCell): void {
    if (cell.events.length === 0) return;
    this.selectedCell = cell;
  }

  closeDay(): void {
    this.selectedCell = null;
  }

  onEventClick(event: AgendaDayResponseDto, e?: Event): void {
    e?.stopPropagation();
    // lessonId != null -> detalhes | lessonId == null -> criar via getLabel
    console.log(event);
  }
}
