import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStore } from '../store/auth-store.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authStore = inject(AuthStore);
  const router = inject(Router);

  const token = authStore.getToken();

  if (token) {
    return true;
  }

  return router.navigate(['/auth/log-in']);
};
