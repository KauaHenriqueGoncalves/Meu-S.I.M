import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { StudentResponseDto } from '../../dto/student-response.dto';
import { catchError, debounceTime, distinctUntilChanged, finalize, Subject, throwError, timeout } from 'rxjs';
import { StudentApi } from '../../api/student-api';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { PlusSvg } from '../../../../shared/components/svg/plus.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { LoupeSvg } from '../../../../shared/components/svg/loupe.svg';
import { PageResponse } from '../../../../shared/models/page-response.model';

@Component({
  selector: 'app-students',
  imports: [
    PlusSvg,
    SpinnerToButton,
    LoupeSvg,
  ],
  templateUrl: './students.html',
  styleUrl: './students.sass',
})
export class Students implements OnInit {
  students: StudentResponseDto[] = [];
  isLoading = true;

  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  isPaginating = false;

  searchName = '';
  searchSubject = new Subject<string>();

  constructor(
    private studentApi: StudentApi,
    private router: Router,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {
    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(name => {
      this.searchName = name;
      this.currentPage = 0;
      this.loadStudents();
    });
  }
  ngOnInit(): void {
    this.loadStudents();
  }

  onSearch(event: any): void {
    this.searchSubject.next(event.target.value);
  }

  loadStudents(): void {
    this.isPaginating = true;
    this.cdr.detectChanges();
    this.studentApi.findAll(this.searchName, this.currentPage, this.pageSize)
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
        next: (res: PageResponse<StudentResponseDto>) => {
          this.students = res.content;
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar estudantes. Por favor, tente novamente mais tarde.'
          });
        }
      });
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadStudents();
    }
  }

  goToCreate(): void {
    this.router.navigate(['/app/create-student']);
  }

  goToDetails(id: string): void {
    this.router.navigate(
      ['/app/details-student'], 
      { state: { id } }
    );
  }

  getInitials(name: string): string {
    if (!name) return 'C';
    const parts = name.split(' ');
    if (parts.length > 1) return (parts[0][0] + parts[1][0]).toUpperCase();
    return parts[0].substring(0, 2).toUpperCase();
  }
}

