import { Routes } from "@angular/router";
import { roleGuard } from "../core/auth/guard/role-guard";
import { classroomRoutes } from "./classroom/classroom.routes";
import { collaboratorRoutes } from "./collaborator/collaborator.routes";
import { configurationRoutes } from "./configuration/configuration.routes";
import { dashboardRoutes } from "./dashboard/dashboard.routes";
import { legalGuardianRoutes } from "./legalguardian/legal-guardian.routes";
import { lessonRoutes } from "./lesson/lesson.routes";
import { schoolAdminRoutes } from "./schooladmin/schooladmin.routes";
import { studentRoutes } from "./student/student.routes";
import { subjectRoutes } from "./subject/subject.routes";
import { subscriptionRoutes } from "./subscription/subscription.routes";

export const privateRoutes: Routes = [
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
        children: lessonRoutes
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