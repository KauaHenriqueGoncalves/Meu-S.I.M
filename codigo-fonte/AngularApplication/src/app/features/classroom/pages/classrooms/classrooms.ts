import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ClassroomApi } from '../../api/classroom-api';
import { ClassroomViewSimpleResponseDto } from '../../dto/classroom-view-simple-response.dto';
import { Router } from '@angular/router';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { PlusSvg } from '../../../../shared/components/svg/plus.svg';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { BookSvg } from '../../../../shared/components/svg/book.svg';

@Component({
  selector: 'app-classrooms',
  imports: [
    SpinnerToButton,
    PlusSvg,
    BookSvg
],
  templateUrl: './classrooms.html',
  styleUrl: './classrooms.sass',
})
export class Classrooms implements OnInit {
  classrooms: ClassroomViewSimpleResponseDto[] = [];
  isLoading = true;

  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  isPaginating = false;

  constructor(
    private classroomApi: ClassroomApi,
    private router: Router,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadClassrooms();
  }

  loadClassrooms(): void {
    this.isLoading = true;
    this.isPaginating = true;
    this.cdr.detectChanges();

    setTimeout(() => {
      this.classrooms = [
        { id: '1', classTypeName: 'Isoladossssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss', subjectName: 'Portuguesssssssssssssss ssssssssssssssssssssssssssssssssss', name: 'Turma 1ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss' },
        { id: '2', classTypeName: 'Isolado', subjectName: 'Portugues', name: 'Turma 1' },
        { id: '3', classTypeName: 'Isolado', subjectName: 'Portugues', name: 'Turma 1' },
        { id: '4', classTypeName: 'Isolado', subjectName: 'Portugues', name: 'Turma 1' },
        { id: '5', classTypeName: 'Isolado', subjectName: 'Portugues', name: 'Turma 1' },
      ];
      this.isLoading = false;
      this.isPaginating = false;
      this.cdr.detectChanges();
    }, 2500);
    // this.classroomApi.findAll(this.currentPage, this.pageSize)
    //   .pipe(
    //     timeout(10000),
    //     catchError((error) => {
    //       return throwError(() => error);
    //     }),
    //     finalize(() => {
    //       this.isLoading = false;
    //       this.isPaginating = false;
    //       this.cdr.detectChanges();
    //     })
    //   )
    //   .subscribe({
    //     next: (res: PageResponse<ClassroomViewSimpleResponseDto>) => {
    //       this.classrooms = res.content;
    //       this.totalElements = res.totalElements;
    //       this.totalPages = res.totalPages;
    //     },
    //     error: (err) => {
    //       this.notificationService.notify({
    //         type: 'error',
    //         text: err.error?.message || 'Erro ao carregar turmas'
    //       });
    //     }
    //   });
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadClassrooms();
    }
  }

  goToCreate(): void {
    this.router.navigate(['/app/create-classroom']);
  }

  goToDetails(id: string): void {
    console.log("Tela de detalhes: " + id);
    // this.router.navigate(
    //   ['/app/details-classroom'], 
    //   { state: { id } }
    // );
  }
}
