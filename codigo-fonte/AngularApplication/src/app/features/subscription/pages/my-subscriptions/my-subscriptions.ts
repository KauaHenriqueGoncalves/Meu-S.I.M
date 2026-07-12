import { DatePipe, NgClass } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { SubscriptionStatus } from '../../enum/subscription-status.enum';
import { SubscriptionResponseDto } from '../../dto/subscription-response.dto';
import { PageResponse } from '../../../../shared/models/page-response.model';
import { SubscriptionApi } from '../../api/subscription.api';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { SpinnerToButton } from '../../../../shared/components/spinner-to-button/spinner-to-button';
import { ArrowLeftSvg } from "../../../../shared/components/svg/icon-arrow-left.svg";

@Component({
  selector: 'app-my-subscriptions',
  imports: [DatePipe, NgClass, RouterLink, SpinnerToButton, ArrowLeftSvg],
  templateUrl: './my-subscriptions.html',
  styleUrl: './my-subscriptions.sass',
})
export class MySubscriptions implements OnInit {
  @ViewChild('subsContainer') subsContainer!: ElementRef;

  Status = SubscriptionStatus;
  isPaginating: boolean = false;

  pageData?: PageResponse<SubscriptionResponseDto>;

  constructor(
    private subscriptionApi: SubscriptionApi,
    private router: Router,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadSubscriptions();
  }

  viewDetails(id: string) {
    this.router.navigate(['/app/subscription-detail'], {
      state: { id }
    });
  }

  changePage(targetPage: number) {
    if (this.isPaginating) return;

    this.isPaginating = true;

    this.subscriptionApi.findAll(targetPage, this.pageData!.size)
      .pipe(
        timeout(10000),
        catchError((error) => {
          return throwError(() => error);
        }),
        finalize(() => {
          this.isPaginating = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res: PageResponse<SubscriptionResponseDto>) => {
          this.pageData = res;
          this.cdr.detectChanges();
          this.moveScroolToDown(this.subsContainer);
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro ao mudar de página'
          });
        }
      });
  }

  private moveScroolToDown(container: ElementRef): void {
    setTimeout(() => {
      let el: HTMLElement | null = container.nativeElement.parentElement;

      while (el) {
        if (el.scrollHeight > el.clientHeight) {
          el.scrollTo({
            top: el.scrollHeight,
            behavior: 'smooth'
          });
          break;
        }
        el = el.parentElement;
      }
    }, 0);
  }

  private loadSubscriptions(): void {
    const initRenderPage: number = 0;
    const initRenderSize: number = 15;
    this.subscriptionApi.findAll(initRenderPage, initRenderSize)
      .pipe(
        timeout(10000),
        catchError((error) => {
          return throwError(() => error);
        }),
        finalize(() => { })
      )
      .subscribe({
        next: (res: PageResponse<SubscriptionResponseDto>) => {
          this.pageData = res;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.notificationService.notify({
            type: 'error',
            text: err.error?.message || 'Erro inesperado, tente novamente mais tarde'
          });
        }
      });
  }
}
