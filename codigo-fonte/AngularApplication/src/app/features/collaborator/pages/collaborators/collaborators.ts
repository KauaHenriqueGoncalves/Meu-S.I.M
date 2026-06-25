import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CollaboratorViewSimplesResponseDto } from '../../dto/collaborator-view-simple-response.dto';
import { catchError, debounceTime, distinctUntilChanged, finalize, Subject, throwError, timeout } from 'rxjs';
import { CollaboratorApi } from '../../api/collaborator.api';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { FormsModule } from '@angular/forms';
import { PlusSvg } from '../../../../shared/components/svg/plus.svg';
import { LoupeSvg } from '../../../../shared/components/svg/loupe.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { NoEmojiDirective } from '../../../../shared/directives/no-emoji.directive';
import { NoSpecialCharacteresDirective } from '../../../../shared/directives/no-special-characteres.directive';

@Component({
  selector: 'app-collaborators',
  imports: [
    FormsModule, 
    PlusSvg,
    LoupeSvg,
    SpinnerToButton,
    NoEmojiDirective,
    NoSpecialCharacteresDirective
  ],
  templateUrl: './collaborators.html',
  styleUrl: './collaborators.sass',
})
export class Collaborators implements OnInit {
  collaborators: CollaboratorViewSimplesResponseDto[] = [];
  isLoading = true;

  currentPage = 0;
  pageSize = 15;
  totalElements = 0;
  totalPages = 0;
  isPaginating = false;

  searchName = '';
  searchSubject = new Subject<string>();

  constructor(
    private collaboratorApi: CollaboratorApi,
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
      this.loadCollaborators();
    });
  }

  ngOnInit(): void {
    this.loadCollaborators();
  }

  onSearch(event: any): void {
    this.searchSubject.next(event.target.value);
  }

  loadCollaborators(): void {
    this.isLoading = true;
    this.isPaginating = true;
    this.cdr.detectChanges();
    this.collaboratorApi.findAll(this.searchName, this.currentPage, this.pageSize)
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
        next: (res: PageResponse<CollaboratorViewSimplesResponseDto>) => {
          this.collaborators = res.content;
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
      this.loadCollaborators();
    }
  }

  goToCreate(): void {
    this.router.navigate(['/app/create-collaborator']);
  }

  goToDetails(id: string): void {
    this.router.navigate(
      ['/app/details-collaborator'], 
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
