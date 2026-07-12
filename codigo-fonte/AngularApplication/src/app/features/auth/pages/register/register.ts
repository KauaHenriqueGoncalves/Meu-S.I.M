import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterStepUser } from "../../components/register-step-user/register-step-user";
import { ArrowLeftSvg } from "../../../../shared/components/svg/icon-arrow-left.svg";
import { RegisterStepSchool } from "../../components/register-step-school/register-step-school";
import { UserRequestDto } from '../../../user/dto/user-request.dto';
import { SchoolRequestDto } from '../../../school/dto/school-request.dto';
import { RegisterStateService } from '../../services/register-state.service';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { CaptchaRequestDto } from '../../dto/capcha-request.dto';
import { SchooladminApi } from '../../../schooladmin/api/schooladmin.api';

declare const turnstile: any;

@Component({
  selector: 'app-register',
  imports: [RegisterStepUser, ArrowLeftSvg, RegisterStepSchool],
  templateUrl: './register.html',
  styleUrl: './register.sass',
})
export class Register implements OnInit, OnDestroy {
  step: number = 0;
  isLoading: boolean = false;

  userData: Partial<UserRequestDto> = {};
  schoolData: Partial<SchoolRequestDto> = {};
  captchaData: Partial<CaptchaRequestDto> = {};

  captchaExecuting: boolean = false;
  private widgetId: string | null = null;

  private readonly SITE_KEY: string = environment.turnstileSiteKey;

  private captchaTimeoutRef: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    private registerStateService: RegisterStateService,
    private notificationService: NotificationService,
    private schoolAdminApi: SchooladminApi
  ) { }

  ngOnInit(): void {
    this.loadTurnstile();
  }

  ngOnDestroy(): void {
    if (this.captchaTimeoutRef) {
      clearTimeout(this.captchaTimeoutRef);
    }
    if (this.widgetId) {
      try {
        turnstile.remove(this.widgetId);
      } 
      catch (e) {
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
      this.notificationService.notify({
        type: 'error',
        text: 'reCAPTCHA não carregado, atualize a página ou volte mais tarde'
      });
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
      retry: 'never',
      callback: (token: string) => {
        if (!this.captchaExecuting) return;

        this.captchaExecuting = false;
        this.captchaData = { captchaToken: token };

        if (!this.userData?.email || !this.schoolData?.nameCode) {
          this.isLoading = false;
          this.notificationService.notify({
            type: 'error',
            text: 'Dados do formulário perdidos, preencha novamente'
          });
          this.backStep();
          return;
        };

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
          text: 'Falha na verificação de reCAPTCHA, tente novamente'
        });

        this.cdr.detectChanges();
      },
    });
  }

  backToHome(): void {
    this.router.navigate(['/']);
  }

  nextStep(data: UserRequestDto) {
    this.userData = data;
    this.step = 1;
  }

  backStep() {
    this.step = 0;
    this.userData = {};
    this.schoolData = {};
  }

  finish(data: SchoolRequestDto) {
    if (this.isLoading) return;
    if (!this.widgetId) return;
    if (this.captchaExecuting) return;

    this.schoolData = data;

    this.isLoading = true;
    this.captchaExecuting = true;
    this.captchaData = { captchaToken: null };

    const state = turnstile.getResponse(this.widgetId);

    if (state !== undefined) {
      turnstile.reset(this.widgetId);
    }

    turnstile.execute(this.widgetId);

    this.captchaTimeoutRef = setTimeout(() => {
      if (this.captchaExecuting) {
        this.notificationService.notify({
          type: 'warning',
          text: 'Verificação de robô travou, tente novamente, resetando...'
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
    if (!this.captchaData?.captchaToken) {
      this.isLoading = false;
      console.error('[Register] submitRegister chamado sem token — isso não deveria acontecer');
      return;
    }

    let success: boolean = false;

    this.schoolAdminApi.create(
      this.userData as UserRequestDto,
      this.schoolData as SchoolRequestDto,
      this.captchaData as CaptchaRequestDto
    )
      .pipe(
        timeout(10000),
        catchError((error) => {
          this.captchaExecuting = false;
          this.isLoading = false;
          this.captchaData = { captchaToken: null };

          if (this.widgetId) {
            turnstile.reset(this.widgetId);
          }

          return throwError(() => error);
        }),
        finalize(() => {
          this.isLoading = false;

          if (!success) {
            this.cdr.detectChanges();
          }
        })
      )
      .subscribe({
        next: () => {
          success = true;
          this.registerStateService.email = this.userData.email!;
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
