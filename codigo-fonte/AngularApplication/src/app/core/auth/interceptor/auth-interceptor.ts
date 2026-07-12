import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, switchMap, throwError, BehaviorSubject, filter, take } from 'rxjs';
import { AuthStore } from '../store/auth-store.service';
import { NO_AUTH } from '../../config/no-auth.token.config';
import { AuthApi } from '../../../features/auth/api/auth.api';
import { Router } from '@angular/router';
import { NotificationService } from '../../services/notification/notification.service';
import { CacheResetService } from '../../services/cache-reset/cache-reset.service';

let isRefreshing = false;

export const refreshTokenSubject = new BehaviorSubject<string | null>(null);
export const isRefreshingSignal = () => isRefreshing;

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authStore = inject(AuthStore);
  const authApi = inject(AuthApi);
  const router = inject(Router);
  const cacheReset = inject(CacheResetService);
  const notificationService = inject(NotificationService);

  // se for rota pública -> ignora tudo 
  if (req.context.get(NO_AUTH)) {
    console.log('[Interceptor] Rota pública (NO_AUTH):', req.url);
    return next(req);
  }

  const token: string | null = authStore.getToken();

  let authReq = req;

  if (token) {
    console.log('[Interceptor] Token aplicado');

    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      },
      withCredentials: true
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status !== 401) {
        console.log('[Interceptor] error diferente → lançar error');
        return throwError(() => error);
      }

      console.log('[Interceptor] 401 detectado → tentando refresh');

      // se já está fazendo refresh -> espera
      if (isRefreshing) {
        console.log('[Interceptor] Já está fazendo refresh → aguardando');

        return refreshTokenSubject.pipe(
          filter((token) => token !== null),
          take(1),
          switchMap((newToken) => {
            const newReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`
              },
              withCredentials: true
            });
            return next(newReq);
          })
        );
      }

      // inicia refresh
      isRefreshing = true;
      refreshTokenSubject.next(null);

      console.log('[Interceptor] Iniciando refresh token...');

      return authApi.refresh().pipe(
        catchError((err) => {
          notificationService.notify({
            type: 'error',
            text: 'Insirá as credenciais da conta.'
          });
          // refresh falhou -> logout
          console.log('[Interceptor] Refresh falhou → logout');
          isRefreshing = false;
          authStore.clear();
          authApi.logout();
          cacheReset.resetAll();
          router.navigate(['/auth/log-in']);
          return throwError(() => err);
        }),
        switchMap((res: any) => {
          const newToken = res.accessToken;

          console.log('[Interceptor] Refresh OK → novo token recebido');

          // salva novo token
          authStore.setToken(newToken);

          // libera fila
          refreshTokenSubject.next(newToken);
          isRefreshing = false;

          // refaz request original
          const newReq = req.clone({
            setHeaders: {
              Authorization: `Bearer ${newToken}`
            },
            withCredentials: true
          });

          return next(newReq);
        }),
        
      );
    })
  );
};
