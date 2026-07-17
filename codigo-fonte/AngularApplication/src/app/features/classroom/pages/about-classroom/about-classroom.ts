import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { BookSvg } from '../../../../shared/components/svg/book.svg';
import { UsersSvg } from '../../../../shared/components/svg/users.svg';
import { ClockSvg } from '../../../../shared/components/svg/clock.svg';
import { Router, RouterOutlet } from "@angular/router";
import { NotificationService } from '../../../../core/services/notification/notification.service';

@Component({
  selector: 'app-about-classroom',
  imports: [
    ArrowLeftSvg,
    BookSvg,
    UsersSvg,
    ClockSvg,
    RouterOutlet
],
  templateUrl: './about-classroom.html',
  styleUrl: './about-classroom.sass',
})
export class AboutClassroom implements OnInit {
  classroomId!: string;
  
  active: string = 'details';

  constructor(
    private notificationService: NotificationService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.classroomId = history.state.id || '';
    if (!this.classroomId) {
      this.goBack();
      this.notificationService.notify({
        type: 'error',
        text: 'Erro inesperado, não carregou a identificação da turma'
      });
      return;
    }
  }

  activeTab(tab: string): void {
    this.active = tab;
    this.cdr.detectChanges();
  }

  goToDetails(): void { 
    this.router.navigate(
      ['/app/about-classroom/details'],
      { state: { id: this.classroomId }, replaceUrl: true }
    );
  }

  goToStudents(): void { 
    this.router.navigate(
      ['/app/about-classroom/students'],
      { state: { id: this.classroomId }, replaceUrl: true }
    );
  }

  goToSchedule(): void {
    this.router.navigate(
      ['/app/about-classroom/schedule'],
      { state: { id: this.classroomId }, replaceUrl: true }
    );
  }

  goBack(): void {
    window.history.back();
  }
}
