import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { LessonDetailViewResponseDto } from '../../../lesson/dto/lesson-detail-view-response.dto';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { LessonApi } from '../../../lesson/api/lesson-api';
import { finalize, timeout } from 'rxjs';

@Component({
  selector: 'app-lessons-details-classroom',
  imports: [],
  templateUrl: './lessons-details-classroom.html',
  styleUrl: './lessons-details-classroom.sass',
})
export class LessonsDetailsClassroom implements OnInit {
  classroomId!: string;
  lessonId!: string;
  detail?: LessonDetailViewResponseDto;
  isLoading = true;

  constructor(
    private lessonApi: LessonApi,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.classroomId = history.state.id || '';
    this.lessonId = history.state.lessonId || '';
    if (!this.classroomId || !this.lessonId) {
      window.history.back();
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, tente novamente mais tarde'
      });
      return;
    }
    this.lessonApi.getDetails(this.classroomId, this.lessonId)
      .pipe(
        timeout(10000),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: LessonDetailViewResponseDto) => {
          this.detail = res;
          console.log(this.detail);
        },
        error: (err: any) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar a aula.'
          });
        }
      });
  }

}
