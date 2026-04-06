import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStore } from '../../auth/store/auth-store.service';

export const loginGuest: CanActivateFn = (route, state) => {
  const authStore = inject(AuthStore);
  const router = inject(Router);
  
  if (authStore.getToken()) {
      router.navigate(['/app']);
      return false;
    }
  
  return true;
};
