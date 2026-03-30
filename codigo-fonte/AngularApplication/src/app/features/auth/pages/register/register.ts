import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterStepUser } from "../../components/register-step-user/register-step-user";
import { IconArrowLeft } from "../../../../shared/components/icons/icon-arrow-left.icon";
import { RegisterStepSchool } from "../../components/register-step-school/register-step-school";
import { UserRequest } from '../../../../core/models/requests/user/user-request.model';
import { SchoolRequest } from '../../../../core/models/requests/school/school-request.model';
import { RegisterStateService } from '../../services/register-state.service';
import { SchooladminApiService } from '../../../../core/services/api/schooladmin/schooladmin.api.service';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { environment } from '../../../../../environments/environment';

declare const turnstile: any;

@Component({
  selector: 'app-register',
  imports: [RegisterStepUser, IconArrowLeft, RegisterStepSchool],
  templateUrl: './register.html',
  styleUrl: './register.sass',
})
export class Register implements OnInit, OnDestroy {
  step: number = 0;
  isLoading: boolean = false;

  userData: any = {};
  schoolData: any = {};
  captchaData: any = {};

  private widgetId: string | null = null;
  private captchaExecuting: boolean = false;

  private readonly SITE_KEY = environment.turnstileSiteKey;

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    private registerStateService: RegisterStateService,
    private notificationService: NotificationService,
    private schoolAdminApi: SchooladminApiService
  ) { }

  ngOnInit(): void {
    this.loadTurnstile();
  }

  ngOnDestroy(): void {
    if (this.widgetId) {
      try {
        turnstile.remove(this.widgetId);
      } catch (e) {
        console.warn('Erro ao remover Turnstile:', e);
      }
      this.widgetId = null;
    }
  }

  private loadTurnstile(): void {
    if (typeof turnstile !== 'undefined' && turnstile.render) {
      this.renderWidget();
      return;
    }

    const script = document.querySelector('script[src*="turnstile"]') as HTMLScriptElement;

    if (!script) {
      console.error('Turnstile script não encontrado');
      return;
    }

    script.addEventListener('load', () => {
      if (!this.widgetId) {
        this.renderWidget();
      }
    }, { once: true });
  }

  private renderWidget(): void {
    this.widgetId = turnstile.render('#turnstile-container', {
      sitekey: this.SITE_KEY,
      size: 'invisible',
      callback: (token: string) => {
        if (!this.captchaExecuting || this.isLoading === false) return;

        this.captchaExecuting = false;
        this.captchaData = { captchaToken: token };

        if (!this.userData?.email || !this.schoolData?.nameCode) return;

        this.submitRegister();
      },
      'expired-callback': () => {
        this.captchaExecuting = false;
        this.captchaData = { captchaToken: null };
      },
      'error-callback': () => {
        this.isLoading = false;
        this.captchaExecuting = false;
        this.captchaData = { captchaToken: null };

        if (this.widgetId) {
          turnstile.reset(this.widgetId);
        }

        this.notificationService.notify({
          type: 'error',
          text: 'Falha na verificação de segurança, tente novamente'
        });

        this.cdr.detectChanges();
      },
    });
  }

  backToHome() {
    this.router.navigate(['/']);
  }

  nextStep(data: UserRequest) {
    this.userData = data;
    this.step = 1;
  }

  backStep() {
    this.step = 0;
    this.userData = {};
    this.schoolData = {};
  }

  finish(data: SchoolRequest) {
    if (this.isLoading) return;
    if (!this.widgetId) return;
    if (this.captchaExecuting) return;

    this.schoolData = data;

    this.isLoading = true;
    this.captchaExecuting = true;
    this.captchaData = { captchaToken: null };

    turnstile.execute(this.widgetId);

    setTimeout(() => {
      if (this.captchaExecuting) {
        this.notificationService.notify({
          type: 'warning',
          text: 'Verificação de robô travou, resetando...'
        });

        this.captchaExecuting = false;
        this.isLoading = false;

        if (this.widgetId) {
          turnstile.reset(this.widgetId);
        }

        this.cdr.detectChanges();
      }
    }, 8000);
  }

  private submitRegister(): void {
    console.log(this.userData);
    console.log(this.schoolData);
    console.log(this.captchaData);

    if (!this.captchaData?.captchaToken) {
      this.isLoading = false;
      return;
    }

    this.schoolAdminApi.create(this.userData, this.schoolData, this.captchaData)
      .pipe(
        timeout(10000),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        }),
        catchError((error) => {
          this.captchaExecuting = false;
          this.isLoading = false;

          this.captchaData = { captchaToken: null };

          if (this.widgetId) {
            turnstile.reset(this.widgetId);
          }

          return throwError(() => error);
        })
      )
      .subscribe({
        next: () => {
          this.registerStateService.email = this.userData.email;
          this.router.navigate(['/auth/verify-account']);
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
