import { ChangeDetectorRef, Component } from '@angular/core';
import { NoSpecialCharacteresDirective } from '../../../../shared/directives/no-special-characteres.directive';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { Router } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, timeout, catchError, throwError, finalize } from 'rxjs';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { LoupeSvg } from '../../../../shared/components/svg/loupe.svg';
import { PlusSvg } from '../../../../shared/components/svg/plus.svg';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { SchooladminApi } from '../../api/schooladmin.api';
import { SchoolAdminSimpleViewResponseDTO } from '../../dto/school-admin-simple-view.response.dto';

@Component({
  selector: 'app-school-admins',
  imports: [
    PlusSvg, 
    LoupeSvg, 
    SpinnerToButton, 
    NoEmojiDirective, 
    NoSpecialCharacteresDirective
  ],
  templateUrl: './school-admins.html',
  styleUrl: './school-admins.sass',
})
export class SchoolAdmins {
  schoolAdmins: SchoolAdminSimpleViewResponseDTO[] = [];
  isLoading = true;

  currentPage = 0;
  pageSize = 15;
  totalElements = 0;
  totalPages = 0;
  isPaginating = false;

  searchName = '';
  searchSubject = new Subject<string>();

  constructor(
    private schoolAdminApi: SchooladminApi,
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
      this.loadSchoolAdmins();
    });
  }

  ngOnInit(): void {
    this.loadSchoolAdmins();
  }

  onSearch(event: any): void {
    this.searchSubject.next(event.target.value);
  }

  loadSchoolAdmins(): void {
    this.isLoading = true;
    this.isPaginating = true;
    this.cdr.detectChanges();
    this.schoolAdminApi.page(this.searchName, this.currentPage, this.pageSize)
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
        next: (res: any) => {
          const page: PageResponse<SchoolAdminSimpleViewResponseDTO> = res;
          this.schoolAdmins = page.content;
          this.totalElements = page.totalElements;
          this.totalPages = page.totalPages;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar administradores'
          });
        }
      });
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadSchoolAdmins();
    }
  }

  goToCreate(): void {
    this.router.navigate(['/app/create-school-admin']);
  }

  goToDetails(id: string): void {
    this.router.navigate(
      ['/app/details-school-admin'],
      { state: { id } }
    );
  }

  getInitials(name: string): string {
    if (!name) return 'A';
    const parts = name.split(' ');
    if (parts.length > 1) return (parts[0][0] + parts[1][0]).toUpperCase();
    return parts[0].substring(0, 2).toUpperCase();
  }
}
