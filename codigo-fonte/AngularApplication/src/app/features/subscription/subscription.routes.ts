import { Routes } from "@angular/router";

export const routes: Routes = [
    {
        path: 'new-subscription',
        loadComponent: () =>
            import('./pages/new-subscription/new-subscription')
                .then(m => m.NewSubscription)
    },
];