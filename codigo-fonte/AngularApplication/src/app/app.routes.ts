import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guard/auth-guard';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () =>
            import('./layout/public-layout/public-layout')
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
            import('./layout/private-layout/private-layout')
                .then(m => m.PrivateLayout),
        children: [
            {
                path: '',
                loadChildren: () => 
                    import('./features/dashboard/dashboard.routes')
                        .then(m => m.routes)
            },
            {
                path: '',
                loadChildren: () => 
                    import('./features/subscription/subscription.routes')
                        .then(m => m.routes)
            }
        ]
    },
    {
        path: '**',
        redirectTo: ''
    },
];
