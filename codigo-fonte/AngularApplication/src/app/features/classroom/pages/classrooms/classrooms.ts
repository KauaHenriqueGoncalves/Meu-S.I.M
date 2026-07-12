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
import { ClassTypeService } from '../../../classtype/service/class-type.service';

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
    private classTypeService: ClassTypeService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadClassrooms();
  }

  getFriendlyClassTypeName(dbName: string): string {
    return this.classTypeService.getFriendlyClassTypeName(dbName);
  }

  loadClassrooms(): void {
    this.isLoading = true;
    this.isPaginating = true;
    this.cdr.detectChanges();
    this.classroomApi.findAll(this.currentPage, this.pageSize)
      .pipe(
        timeout(10000),
        catchError((error) => {
          return throwError(() => error);
        }),
        finalize(() => {
          this.isLoading = false;
          this.isPaginating = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: PageResponse<ClassroomViewSimpleResponseDto>) => {
          this.classrooms = res.content;
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar turmas'
          });
        }
      });
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
    this.router.navigate(
      ['/app/details-classroom'], 
      { state: { id } }
    );
  }
}
