import { Routes } from '@angular/router';
import { AdminBillingDiscounts } from './pages/admin-billing-discounts/admin-billing-discounts';

export const adminBillingDiscountRoutes: Routes = [
    {
        path: 'billing-discount',
        component: AdminBillingDiscounts
    }
];