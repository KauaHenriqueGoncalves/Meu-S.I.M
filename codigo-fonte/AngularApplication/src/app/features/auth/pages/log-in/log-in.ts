import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ArrowLeftSvg } from '../../../../shared/components/svg/icon-arrow-left.svg';
import { Router } from '@angular/router';
import { LogInUser } from "../../components/log-in-user/log-in-user";
import { LoginRequestDto } from '../../dto/login-request.dto';
import { CaptchaRequestDto } from '../../dto/capcha-request.dto';
import { AuthApi } from '../../api/auth.api';
import { NotificationService } from '../../../../core/services/notification/notification.service';
import { catchError, finalize, throwError, timeout } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { AuthStore } from '../../../../core/auth/store/auth-store.service';
import { TokenResponse } from '../../dto/token-response.dto';

declare const turnstile: any;

@Component({
  selector: 'app-log-in',
  imports: [ArrowLeftSvg, LogInUser],
  templateUrl: './log-in.html',
  styleUrl: './log-in.sass',
})
export class LogIn implements OnInit, OnDestroy {
  isLoading: boolean = false;

  loginData: Partial<LoginRequestDto> = {}
  captchaData: Partial<CaptchaRequestDto> = {};

  captchaExecuting: boolean = false;
  private widgetId: string | null = null;

  private readonly SITE_KEY: string = environment.turnstileSiteKey;

  private captchaTimeoutRef: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    private authStore: AuthStore,
    private notificationService: NotificationService,
    private authApi: AuthApi
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

        if (!this.loginData) {
          this.isLoading = false;
          this.notificationService.notify({
            type: 'error',
            text: 'Dados do formulário perdidos, preencha novamente'
          });
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

  finish(loginRequest: LoginRequestDto): void {
    if (this.isLoading) return;
    if (!this.widgetId) return;
    if (this.captchaExecuting) return;

    this.loginData = loginRequest;

    this.isLoading = true;
    this.captchaExecuting = true;
    this.captchaData = { captchaToken: null };

    const state: any = turnstile.getResponse(this.widgetId);

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
      console.error('[Register] submitRegister chamado sem token - isso não deveria acontecer');
      return;
    }

    let success: boolean = false;

    this.authApi.login(
      this.loginData as LoginRequestDto,
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
          this.captchaExecuting = false;

          if (!success) {
            this.cdr.detectChanges();
          }
        })
      )
      .subscribe({
        next: (res: TokenResponse) => {
          success = true;
          this.authStore.setToken(res.accessToken);
          this.router.navigate(['/app/dashboard'], { replaceUrl: true });
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
