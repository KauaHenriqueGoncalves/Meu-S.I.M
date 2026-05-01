import { Routes } from "@angular/router";

export const routes: Routes = [
    {
        path: 'new-subscription',
        loadComponent: () =>
            import('./pages/new-subscription/new-subscription')
                .then(m => m.NewSubscription)
    },
    {
        path: 'my-subscriptions',
        loadComponent: () =>
            import('./pages/my-subscriptions/my-subscriptions')
                .then(m => m.MySubscriptions)
    },
    {
        path: 'subscription-detail',
        loadComponent: () =>
            import('./pages/subscription-detail/subscription-detail')
                .then(m => m.SubscriptionDetail)
    }
];