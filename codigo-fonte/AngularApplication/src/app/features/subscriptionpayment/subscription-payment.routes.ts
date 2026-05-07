import { Routes } from "@angular/router";
import { PaymentStatusPending } from "./pages/payment-status-pending/payment-status-pending";
import { PaymentStatusFailure } from "./pages/payment-status-failure/payment-status-failure";
import { PaymentStatusSuccess } from "./pages/payment-status-success/payment-status-success";

export const subscriptionPaymentRoutes: Routes = [
    {
        path: 'status',
        children: [
            {
                path: 'success',
                component: PaymentStatusSuccess
            },
            {
                path: 'pending',
                component: PaymentStatusPending
            },
            {
                path: 'failure',
                component: PaymentStatusFailure
            }
        ]
    }
];