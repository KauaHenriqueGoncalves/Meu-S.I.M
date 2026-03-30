import { Routes } from '@angular/router';
import { registerFlowGuard } from '../../core/guards/register-flow/register-flow-guard';

export const routes: Routes = [
    {
        path: 'sign-in',
        loadComponent: () =>
            import('./pages/register/register')
                .then(m => m.Register)
    },
    {
        path: 'verify-account',
        canActivate: [registerFlowGuard],
        loadComponent: () =>
            import('./pages/verify-account/verify-account')
                .then(m => m.VerifyAccount)
    }
];