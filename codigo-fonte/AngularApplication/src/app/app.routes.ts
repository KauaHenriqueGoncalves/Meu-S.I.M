import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guard/auth-guard';
import { authRoutes } from './features/auth/auth.routes';
import { PublicLayout } from './layout/public-layout/public-layout';
import { publicRoutes } from './features/public/public.routes';
import { PrivateLayout } from './layout/private-layout/private-layout';
import { dashboardRoutes } from './features/dashboard/dashboard.routes';
import { roleGuard } from './core/auth/guard/role-guard';
import { subscriptionRoutes } from './features/subscription/subscription.routes';
import { subscriptionPaymentRoutes } from './features/subscriptionpayment/subscription-payment.routes';
import { collaboratorRoutes } from './features/collaborator/collaborator.routes';
import { schoolAdminRoutes } from './features/schooladmin/schooladmin.routes';
import { legalGuardianRoutes } from './features/legalguardian/legal-guardian.routes';
import { subjectRoutes } from './features/subject/subject.routes';
import { studentRoutes } from './features/student/student.routes';
import { classroomRoutes } from './features/classroom/classroom.routes';
import { configurationRoutes } from './features/configuration/configuration.routes';

export const routes: Routes = [
    {
        path: '',
        component: PublicLayout,
        children: [
            {
                path: '',
                children: publicRoutes
            }
        ]
    },
    {
        path: 'auth',
        children: authRoutes
    },
    {
        path: 'payment',
        canActivate: [authGuard],
        canMatch: [roleGuard('school_admin')],
        children: subscriptionPaymentRoutes
    },
    {
        path: 'admin/app',
        canActivate: [authGuard],
        component: PrivateLayout,
        children: [
            {
                path: '',
                canMatch: [roleGuard('system_admin')],
                loadChildren: () => import("./features/admin/admin-dashboard/dashboard.routes")
                    .then(m => m.adminDashboardRoutes)
            },
            {
                path: '',
                canMatch: [roleGuard('system_admin')],
                loadChildren: () => import("./features/admin/admin-billingdiscount/admin-billingdiscount.routes")
                    .then(m => m.adminBillingDiscountRoutes)
            },
            {
                path: '',
                canMatch: [roleGuard('system_admin')],
                loadChildren: () => import("./features/admin/admin-schoolplans/schoolplan.routes")
                    .then(m => m.adminSchoolPlanRoutes)
            }
        ]
    },
    {
        path: 'app',
        canActivate: [authGuard],
        component: PrivateLayout,
        children: [
            {
                path: '',
                canMatch: [roleGuard('school_admin', 'collaborator', 'legal_guardian')],
                children: dashboardRoutes
            },
            {
                path: '',
                canMatch: [roleGuard('school_admin')],
                children: classroomRoutes
            },
            {
                path: '',
                canMatch: [roleGuard('school_admin')],
                children: subjectRoutes
            },
            {
                path: '',
                canMatch: [roleGuard('school_admin')],
                children: studentRoutes
            },
            {
                path: '',
                canMatch: [roleGuard('school_admin')],
                children: collaboratorRoutes
            },
            {
                path: '',
                canMatch: [roleGuard('school_admin')],
                children: legalGuardianRoutes
            },
            {
                path: '',
                canMatch: [roleGuard('school_admin')],
                children: schoolAdminRoutes
            },
            {
                path: '',
                canMatch: [roleGuard('school_admin', 'dfasdfasdcasdf')],
                children: subscriptionRoutes
            },
            {
                path: '',
                canMatch: [roleGuard('school_admin', 'collaborator', 'legal_guardian')],
                children: configurationRoutes
            }
        ]
    },
    {
        path: '**',
        redirectTo: ''
    },
];
