import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { PlusSvg } from '../../../../shared/components/svg/plus.svg';
import { TrashSvg } from '../../../../shared/components/svg/trash.svg';
import { ScheduleResponseDto } from '../../../schedule/dto/schedule-response.dto';
import { ScheduleApi } from '../../../schedule/api/schedule-api';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { ScheduleRequestDto } from '../../../schedule/dto/schedule-request.dto';
import { ScheduleService } from '../../../schedule/service/schedule.service';
import { PositionedSchedule } from '../../../schedule/interface/positioned-schedule.interface';

@Component({
  selector: 'app-schedule-classroom',
  imports: [
    ReactiveFormsModule,
    SpinnerToButton,
    PlusSvg,
    TrashSvg
  ],
  templateUrl: './schedule-classroom.html',
  styleUrl: './schedule-classroom.sass',
})
export class ScheduleClassroom implements OnInit {
  classroomId!: string;

  isLoading = true;
  isSubmitting = false;

  schedules: ScheduleResponseDto[] = [];
  weekdays = ScheduleService.weeks;

  readonly pxPerMinute = 1.2; // 72px por hora

  startHour = 7;
  endHour = 19;
  hourMarks: { label: string; top: number }[] = [];
  gridHeight = 0;
  positionedGrid: Record<string, PositionedSchedule[]> = {};

  isModalOpen = false;
  isDeleteModalOpen = false;

  scheduleForm!: FormGroup;
  editingId: string | null = null;
  deletingId: string | null = null;

  weekdayOptions = ScheduleService.weeks;

  private formValues: any;

  constructor(
    private fb: FormBuilder,
    private scheduleApi: ScheduleApi,
    private scheduleService: ScheduleService,
    private notificationService: NotificationService,
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
    this.initForm();
    this.loadSchedules();
  }

  getFriendlyWeekday(day: string): string {
    return this.scheduleService.getFriendlyWeekday(day);
  }

  get isFormChanged(): boolean {
    if (!this.formValues) return false;
    return JSON.stringify(this.scheduleForm.getRawValue()) !== JSON.stringify(this.formValues);
  }

  initForm(): void {
    this.scheduleForm = this.fb.group({
      weekday: [null, Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required]
    }, {
      validators: [this.timeRangeValidator()]
    });
  }

  timeRangeValidator() {
    return (group: FormGroup) => {
      const start = group.get('startTime')?.value;
      const end = group.get('endTime')?.value;
      if (start && end && start >= end) {
        return { invalidRange: true };
      }
      return null;
    };
  }

  isFieldInvalid(field: string): boolean {
    const control = this.scheduleForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  loadSchedules(): void {
    this.isLoading = true;
    this.scheduleApi.findAll(this.classroomId)
      .pipe(
        timeout(10000),
        catchError((error) => throwError(() => error)),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: ScheduleResponseDto[]) => {
          const newRes: ScheduleResponseDto[] = [];
          res.forEach((s) => {
            const schedule = s;
            schedule.weekday = this.scheduleService.getWeekdayByDictionary(s.weekday);
            newRes.push(schedule);
          });
          this.schedules = newRes;
          this.buildGrid();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar os horários.'
          });
        }
      });
  }

  private buildGrid(): void {
    [this.startHour, this.endHour] = this.scheduleService.getHourRange(this.schedules);
    this.hourMarks = this.scheduleService.getHourMarks(this.startHour, this.endHour, this.pxPerMinute);
    this.gridHeight = (this.endHour - this.startHour) * 60 * this.pxPerMinute;
    this.positionedGrid = this.scheduleService.buildPositionedGrid(this.schedules, this.startHour, this.pxPerMinute);
  }

  openModal(schedule?: ScheduleResponseDto): void {
    if (schedule) {
      this.editingId = schedule.id;
      this.scheduleForm.patchValue({
        weekday: schedule.weekday,
        startTime: schedule.startTime,
        endTime: schedule.endTime
      });
    } else {
      this.editingId = null;
      this.scheduleForm.reset();
    }
    this.formValues = this.scheduleForm.getRawValue();
    this.isModalOpen = true;
  }

  closeModal(): void {
    if (this.isSubmitting) return;
    this.isModalOpen = false;
    this.editingId = null;
    this.scheduleForm.reset();
    this.formValues = null;
  }

  onSaveSchedule(): void {
    if (this.scheduleForm.invalid) {
      this.scheduleForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const formValue = this.scheduleForm.value;

    const payload: ScheduleRequestDto = {
      weekday: formValue.weekday,
      startTime: formValue.startTime,
      endTime: formValue.endTime
    };

    const request$ = this.editingId
      ? this.scheduleApi.update(this.classroomId, this.editingId, payload)
      : this.scheduleApi.create(this.classroomId, payload);

    request$.pipe(
      timeout(10000),
      finalize(() => {
        this.isSubmitting = false;
        this.formValues = null;
        this.closeModal();
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: () => {
        this.loadSchedules();
        this.notificationService.notify({
          type: 'success',
          text: this.editingId ? 'Horário atualizado com sucesso!' : 'Horário criado com sucesso.'
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

  openDeleteModal(id: string, event: Event): void {
    event.stopPropagation();
    this.deletingId = id;
    this.isDeleteModalOpen = true;
  }

  closeDeleteModal(): void {
    if (this.isSubmitting) return;
    this.isDeleteModalOpen = false;
    this.deletingId = null;
  }

  confirmDelete(): void {
    if (!this.deletingId) return;

    this.isSubmitting = true;
    this.scheduleApi.delete(this.classroomId, this.deletingId)
      .pipe(
        finalize(() => {
          this.isSubmitting = false;
          this.closeDeleteModal();
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.loadSchedules();
          this.notificationService.notify({
            type: 'success',
            text: 'Horário excluído com sucesso.'
          });
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao excluir o horário.'
          });
        }
      });
  }
}
