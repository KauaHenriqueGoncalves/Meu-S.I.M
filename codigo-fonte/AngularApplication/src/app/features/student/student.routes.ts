import { Routes } from "@angular/router";
import { Students } from "./pages/students/students";
import { CreateStudent } from "./pages/create-student/create-student";

export const studentRoutes: Routes = [
    {
        path: 'students',
        component: Students
    },
    {
        path: 'create-student',
        component: CreateStudent
    }
];