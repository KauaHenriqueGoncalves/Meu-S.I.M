import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AgendaDayResponseDto } from '../../dto/agenda-day-response.dto';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngleDownSvg } from '../../../../shared/components/svg/angle-down.svg';
import { ClassroomViewSimpleResponseDto } from '../../../classroom/dto/classroom-view-simple-response.dto';
import { LessonDisplayStatus } from '../../enum/lesson-display-status.enum';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { ClassroomApi } from '../../../classroom/api/classroom-api'; 2
import { LessonApi } from '../../api/lesson-api';
import { timeout, catchError, throwError, finalize } from 'rxjs';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { AttendanceStatus } from '../../enum/attendance-status.enum';
import { NoteType } from '../../enum/note-type.enum';
import { LabelToCreateLessonResponseDto } from '../../dto/label-to-create-lesson-response.dto';
import { CreateLessonRequestDto } from '../../dto/create-lesson-request.dto';
import { GetToCreateLessonRequestDto } from '../../dto/get-to-create-lesson-request.dto';
import { LessonDetailViewResponseDto } from '../../dto/lesson-detail-view-response.dto';
import { DatePipe } from '@angular/common';

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
    DatePipe,
    ReactiveFormsModule,
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

  readonly attendanceStatuses = Object.values(AttendanceStatus);

  readonly noteTypes = Object.values(NoteType);

  readonly attendanceLabels: Record<string, string> = {
    PRESENT: 'Presente', ABSENT: 'Ausente', JUSTIFIED: 'Justificado'
  };

  readonly noteLabels: Record<string, string> = {
    INCIDENT: 'Incidente', OBSERVATION: 'Observação', FEEDBACK: 'Feedback'
  };

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
  isLabelModalOpen = false;
  isLoadingLabel = false;
  isSubmitting = false;
  detail?: LessonDetailViewResponseDto;
  isDetailOpen = false;
  isLoadingDetail = false;
  lessonForm!: FormGroup;

  constructor(
    private classroomApi: ClassroomApi,
    private lessonApi: LessonApi,
    private notificationService: NotificationService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.buildCalendar();
    this.loadClassrooms();
  }

  get attendances(): FormArray {
    return this.lessonForm.get('attendances') as FormArray;
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
          if (this.classrooms.length > 0) {
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

  private openLabel(event: AgendaDayResponseDto): void {
    this.selectedCell = null;
    this.isLabelModalOpen = true;
    this.isLoadingLabel = true;
    this.lessonForm = this.fb.group({ attendances: this.fb.array([]) });
    this.cdr.detectChanges();
    const payload: GetToCreateLessonRequestDto = {
      scheduleId: event.scheduleId,
      lessonDate: event.date
    }
    this.lessonApi.getLabel(this.classroomId, payload)
      .pipe(
        timeout(10000),
        finalize(() => { this.isLoadingLabel = false; this.cdr.detectChanges(); })
      )
      .subscribe({
        next: (label: any) => {
          this.buildForm(label)
        },
        error: (err: any) => {
          this.closeLabel();
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar o formulário da aula.'
          });
        }
      });
  }

  private buildForm(label: LabelToCreateLessonResponseDto): void {
    this.lessonForm = this.fb.group({
      scheduleId: [label.scheduleId],
      classroomId: [label.classroomId],
      lessonDate: [label.lessonDate],
      isCanceled: [label.isCanceled],
      description: [label.description ?? '', Validators.maxLength(500)],
      attendances: this.fb.array(label.attendances.map(a => this.fb.group({
        studentId: [a.studentId],
        studentName: [a.studentName],
        status: [a.status, Validators.required],
        content: [a.content ?? '', Validators.maxLength(300)],
        hasNote: [!!a.lessonNote],
        note: this.fb.group({
          type: [a.lessonNote?.type ?? NoteType.OBSERVATION],
          subtype: [a.lessonNote?.subtype ?? '', Validators.maxLength(100)],
          content: [a.lessonNote?.content ?? '', Validators.maxLength(500)],
        })
      })))
    });
    this.cdr.detectChanges();
  }

  closeLabel(): void {
    if (this.isSubmitting) return;
    this.isLabelModalOpen = false;
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

  openDetail(lessonId: string): void {
    this.isDetailOpen = true;
    this.isLoadingDetail = true;
    this.detail = undefined;
    this.cdr.detectChanges();

    this.lessonApi.getDetails(this.classroomId, lessonId)
      .pipe(
        timeout(10000),
        catchError((e) => throwError(() => e)),
        finalize(() => {
          this.isLoadingDetail = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: LessonDetailViewResponseDto) => {
          this.detail = res;
        },
        error: (err: any) => {
          this.isDetailOpen = false;
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar detalhes da aula.'
          });
        }
      });
  }

  closeDetail(): void {
    this.isDetailOpen = false;
    this.detail = undefined;
  }

  onEventClick(event: AgendaDayResponseDto, e?: Event): void {
    e?.stopPropagation();
    if (event.lessonId) {
      this.openDetail(event.lessonId);
      return;
    }
    const dateLesson = new Date(event.date);
    const today = new Date();
    const isFuture = dateLesson > today;
    if (isFuture) {
      this.notificationService.notify({
        type: 'error',
        text: 'Não é possivel fazer agenda de data futura!'
      });
      return;
    }
    this.openLabel(event);
  }

  onSaveLesson(): void {
    if (this.lessonForm.invalid) {
      this.lessonForm.markAllAsTouched();
      return;
    }
    const v = this.lessonForm.getRawValue();
    const payload: CreateLessonRequestDto = {
      scheduleId: v.scheduleId,
      classroomId: v.classroomId,
      lessonDate: v.lessonDate,
      isCanceled: v.isCanceled,
      description: v.description?.trim() ?? '',
      attendances: v.attendances.map((a: any) => ({
        studentId: a.studentId,
        studentName: a.studentName,
        status: a.status,
        content: a.content?.trim() ?? '',
        lessonNote: a.hasNote && a.note.content?.trim()
          ? { type: a.note.type, subtype: a.note.subtype?.trim() ?? '', content: a.note.content.trim() }
          : null
      }))
    };
    this.isSubmitting = true;
    this.lessonApi.create(payload)
      .pipe(
        timeout(10000),
        finalize(() => { this.isSubmitting = false; this.cdr.detectChanges(); })
      )
      .subscribe({
        next: () => {
          this.isLabelModalOpen = false;
          this.notificationService.notify({ type: 'success', text: 'Aula registrada com sucesso!' });
          this.loadAgenda();
        },
        error: (err) => this.notificationService.notify({
          type: 'error',
          text: err.error?.message || 'Erro ao registrar a aula.'
        })
      });
  }
}
