import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../../../core/auth/service/auth.service';

export const loginGuest: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const payload = authService.getPayload();
  const role = payload?.scope;

  console.log(role);

  if (role === 'school_admin') {
    router.navigate(['/app/dashboard']);
    return false;
  }

  if (role === 'system_admin') {
    router.navigate(['/admin/app/dashboard']);
    return false;
  }

  if (role === 'collaborator') {
    router.navigate(['/app/dashboard']);
    return false;
  }

  if (role === 'legal_guardian') {
    router.navigate(['/app/dashboard']);
    return false;
  }
  
  return true;
};
