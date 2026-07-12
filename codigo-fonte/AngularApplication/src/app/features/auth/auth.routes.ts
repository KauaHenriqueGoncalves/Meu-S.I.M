import { Routes } from '@angular/router';
import { loginGuest } from './guards/login-guest/login-guest-guard';
import { Register } from './pages/register/register';
import { LogIn } from './pages/log-in/log-in';
import { VerifyAccount } from './pages/verify-account/verify-account';
import { VerifyAccountSuccess } from './pages/verify-account-success/verify-account-success';
import { VerifyAccountFailed } from './pages/verify-account-failed/verify-account-failed';
import { registerFlowGuard } from './guards/register-flow/register-flow-guard';

export const authRoutes: Routes = [
    {
        path: 'sign-up', 
        component: Register
    },
    {
        path: 'log-in',
        canActivate: [loginGuest],
        component: LogIn
    },
    {
        path: 'verify-account',
        canActivate: [registerFlowGuard],
        component: VerifyAccount,
    },
    {
        path: 'verify-account',
        children: [
            {
                path: 'success',
                component: VerifyAccountSuccess
            },
            {
                path: 'failed',
                component: VerifyAccountFailed
            }
        ]
    }
];