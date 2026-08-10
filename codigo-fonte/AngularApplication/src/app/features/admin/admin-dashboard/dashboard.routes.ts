import { Routes } from '@angular/router';
import { AdminDashboard } from './pages/admin-dashboard/admin-dashboard';

export const adminDashboardRoutes: Routes = [
    {
        path: 'dashboard',
        component: AdminDashboard
    }
];