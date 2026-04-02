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