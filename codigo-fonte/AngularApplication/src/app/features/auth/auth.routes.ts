import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: 'sign-in',
        loadComponent: () =>
            import('./pages/register/register')
                .then(m => m.Register)
    }
];