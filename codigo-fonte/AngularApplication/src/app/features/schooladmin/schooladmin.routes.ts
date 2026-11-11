import { Routes } from "@angular/router";
import { SchoolAdmins } from "./pages/school-admins/school-admins";
import { DetailsSchoolAdmin } from "./pages/details-school-admin/details-school-admin";
import { CreateSchoolAdmin } from "./pages/create-school-admin/create-school-admin";

export const schoolAdminRoutes: Routes = [
    {
        path: 'school-admins',
        component: SchoolAdmins
    },
    {
        path: 'details-school-admin',
        component: DetailsSchoolAdmin
    },
    {
        path: 'create-school-admin',
        component: CreateSchoolAdmin
    }
];