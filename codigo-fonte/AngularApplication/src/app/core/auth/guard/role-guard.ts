import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { TokenPayload } from '../payload/token.payload';
import { AuthStore } from '../store/auth-store.service';

export function roleGuard(...roles: string[]): CanMatchFn {
  return () => {
    const authService: AuthService = inject(AuthService);
    const authStore: AuthStore = inject(AuthStore);
    const router: Router = inject(Router);
    const payload: TokenPayload | null | undefined = authService.getPayload();
    
    if (!payload) {
      authStore.clear();
      return router.navigate(['/auth/log-in']);
    }

    const role: string | null | undefined = payload.scope;

    if (!role) {
      return router.navigate(['/auth/log-in']);
    }

    return roles.includes(role);
  };
}
