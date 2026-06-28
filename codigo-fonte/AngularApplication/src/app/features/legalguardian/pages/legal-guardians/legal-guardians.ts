import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { LegalGuardianViewSimpleResponseDto } from '../../dto/legal-guardian-view-simple-response.dto';
import { catchError, debounceTime, distinctUntilChanged, finalize, Subject, throwError, timeout } from 'rxjs';
import { LegalGuardianApi } from '../../api/legal-guardian-api';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { LoupeSvg } from '../../../../shared/components/svg/loupe.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { PlusSvg } from '../../../../shared/components/svg/plus.svg';
import { PageResponse } from '../../../../shared/models/page-response.model';

@Component({
  selector: 'app-legal-guardians',
  imports: [
    LoupeSvg,
    SpinnerToButton,
    PlusSvg
  ],
  templateUrl: './legal-guardians.html',
  styleUrl: './legal-guardians.sass',
})
export class LegalGuardians implements OnInit {
  legalGuardians: LegalGuardianViewSimpleResponseDto[] = [];
  isLoading = true;

  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;
  isPaginating = false;

  searchName = '';
  searchSubject = new Subject<string>();

  constructor(
    private legalGuardianApi: LegalGuardianApi,
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
      this.loadLegalGuardians();
    });
  }

  ngOnInit(): void {
    this.loadLegalGuardians()
  }

  onSearch(event: any): void {
    this.searchSubject.next(event.target.value);
  }

  loadLegalGuardians(): void {
    this.isLoading = true;
    this.isPaginating = true;
    this.cdr.detectChanges();
    this.legalGuardianApi.findAll(this.searchName, this.currentPage, this.pageSize)
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
        next: (res: PageResponse<LegalGuardianViewSimpleResponseDto>) => {
          this.legalGuardians = res.content;
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao carregar colaboradores'
          });
        }
      });
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadLegalGuardians()
    }
  }

  goToCreate(): void {
    this.router.navigate(['/app/create-legal-guardian']);
  }

  goToDetails(id: string): void {
    this.router.navigate(
      ['/app/details-legal-guardian'], 
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
