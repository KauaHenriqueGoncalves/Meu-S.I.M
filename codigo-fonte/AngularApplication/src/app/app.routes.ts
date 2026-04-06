import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guard/auth-guard';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () =>
            import('./layout/layouts/public/public-layout')
                .then(m => m.PublicLayout),
        children: [
            {
                path: '',
                loadChildren: () =>
                    import('./features/public/public.routes')
                        .then(m => m.routes)
            }
        ]
    },
    {
        path: 'auth',
        loadChildren: () =>
            import('./features/auth/auth.routes')
                .then(m => m.routes)
    },
    {
        path: 'app',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./layout/layouts/private/private-layout')
                .then(m => m.PrivateLayout)
    },
    {
        path: '**',
        redirectTo: ''
    }
];
