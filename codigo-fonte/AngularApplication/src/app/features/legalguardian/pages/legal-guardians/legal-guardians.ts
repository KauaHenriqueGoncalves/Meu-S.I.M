import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { LegalGuardianViewSimpleResponseDto } from '../../dto/legal-guardian-view-simple-response.dto';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { LegalGuardianApi } from '../../api/legal-guardian-api';
import { Router } from '@angular/router';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { LoupeSvg } from '../../../../shared/components/svg/loupe.svg';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { PlusSvg } from '../../../../shared/components/svg/plus.svg';

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
      this.loadMockData();
      //this.loadLegalGuardians();
    });
  }

  ngOnInit(): void {
    this.loadMockData();
    console.log(this.legalGuardians)
    //this.loadLegalGuardians();
  }

  onSearch(event: any): void {
    this.searchSubject.next(event.target.value);
  }

  loadLegalGuardians(): void {
  }

  loadMockData(): void {
    this.isLoading = false;
    // this.isPaginating = true;
    this.legalGuardians = [
      { id: '1', username: 'Ricardo Silva', degreeOfKinship: 'Pai' },
      { id: '2', username: 'Ana Beatriz Souza', degreeOfKinship: 'Tia' },
      { id: '3', username: 'Carlos Eduardo Oliveira', degreeOfKinship: 'Tio' },
      { id: '4', username: 'Mariana Costa', degreeOfKinship: 'Responsável' },
      { id: '5', username: 'Lucas Ferreira', degreeOfKinship: 'doido' },
      { id: '6', username: 'Juliana Pires', degreeOfKinship: 'pena' },
      { id: '7', username: 'Roberto Almeida Jr.', degreeOfKinship: 'pai' },
      { id: '8', username: 'Fernanda Lima', degreeOfKinship: 'mãe' },
      { id: '2', username: 'Ana Beatriz Souza', degreeOfKinship: 'mae' },
      { id: '3', username: 'Carlos Eduardo Oliveira', degreeOfKinship: 'pai adotivo' },
      { id: '4', username: 'Mariana Costa', degreeOfKinship: 'mae' },
      { id: '5', username: 'Lucas Ferreira', degreeOfKinship: 'mae' },
      { id: '6', username: 'Juliana Pires', degreeOfKinship: 'pai' },
      { id: '7', username: 'Roberto Almeida Jr.', degreeOfKinship: 'Só jesus sabe' },
    ];
    this.totalElements = 8;
    this.totalPages = 0;
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadMockData();
    }
  }

  goToCreate(): void {
    console.log('ir para a tela de criação');
    //this.router.navigate(['/app/create-collaborator']);
  }

  goToDetails(id: string): void {
    console.log('ir para a tela de detalhes, id: ' + id);
    // this.router.navigate(
    //   ['/app/details-collaborator'], 
    //   { state: { id } }
    // );
  }

  getInitials(name: string): string {
    if (!name) return 'C';
    const parts = name.split(' ');
    if (parts.length > 1) return (parts[0][0] + parts[1][0]).toUpperCase();
    return parts[0].substring(0, 2).toUpperCase();
  }
}
