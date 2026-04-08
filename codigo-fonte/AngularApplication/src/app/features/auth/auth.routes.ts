import { Routes } from '@angular/router';
import { registerFlowGuard } from '../../core/guards/register-flow/register-flow-guard';
import { loginGuest } from './guards/login-guest-guard';

export const routes: Routes = [
    {
        path: 'sign-up',
        loadComponent: () =>
            import('./pages/register/register')
                .then(m => m.Register)
    },
    {
        path: 'log-in',
        canActivate: [loginGuest],
        loadComponent: () =>
            import('./pages/log-in/log-in')
                .then(m => m.LogIn)
    },
    {
        path: 'verify-account',
        canActivate: [registerFlowGuard],
        loadComponent: () =>
            import('./pages/verify-account/verify-account')
                .then(m => m.VerifyAccount),
    },
    {
        path: 'verify-account',
        children: [
            {
                path: 'success',
                loadComponent: () =>
                    import('./pages/verify-account-success/verify-account-success')
                        .then(m => m.VerifyAccountSuccess)
            },
            {
                path: 'failed',
                loadComponent: () =>
                    import('./pages/verify-account-failed/verify-account-failed')
                        .then(m => m.VerifyAccountFailed)
            }
        ]
    }
];