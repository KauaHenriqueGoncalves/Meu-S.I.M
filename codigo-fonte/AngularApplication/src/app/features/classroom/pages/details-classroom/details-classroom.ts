import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';

@Component({
  selector: 'app-details-classroom',
  imports: [],
  templateUrl: './details-classroom.html',
  styleUrl: './details-classroom.sass',
})
export class DetailsClassroom implements OnInit {
  classroomId!: string;

  constructor(
    private notificationService: NotificationService,
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
  }

  goBack(): void {
    this.router.navigate(['/app/classrooms']);
  }
}
