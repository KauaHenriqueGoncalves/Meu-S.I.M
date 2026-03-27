import { Routes } from '@angular/router';

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
        path: '',
        loadChildren: () =>
            import('./features/auth/auth.routes')
                .then(m => m.routes)
    }
];
