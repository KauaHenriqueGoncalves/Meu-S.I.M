import { Routes } from "@angular/router";
import { NewSubscription } from "./pages/new-subscription/new-subscription";
import { MySubscriptions } from "./pages/my-subscriptions/my-subscriptions";
import { SubscriptionDetail } from "./pages/subscription-detail/subscription-detail";

export const subscriptionRoutes: Routes = [
    {
        path: 'new-subscription',
        component: NewSubscription
    },
    {
        path: 'my-subscriptions',
        component: MySubscriptions
    },
    {
        path: 'subscription-detail',
        component: SubscriptionDetail
    }
];