import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { TokenPayload } from '../payload/token.payload';

export function roleGuard(...roles: string[]): CanMatchFn {
  return () => {
    const authService: AuthService = inject(AuthService);
    const router: Router = inject(Router);
    const payload: TokenPayload | null | undefined = authService.getPayload();
    
    if (!payload) {
      return router.navigate(['/']);
    }

    const role: string | null | undefined = payload.scope;

    if (!role) {
      return router.navigate(['/']);
    }

    console.log(role)
    console.log("Pode carregar: " + roles.includes(role))

    return roles.includes(role);
  };
}
