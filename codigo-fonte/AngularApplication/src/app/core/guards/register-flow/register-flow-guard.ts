import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RegisterStateService } from '../../../features/auth/services/register-state.service';

export const registerFlowGuard: CanActivateFn = () => {
  const registerStateService = inject(RegisterStateService);
  const router = inject(Router);

  if (!registerStateService.email) {
    router.navigate(['/']);
    return false;
  }

  return true;
};
