import { Routes } from "@angular/router";
import { Classrooms } from "./pages/classrooms/classrooms";
import { CreateClassroom } from "./pages/create-classroom/create-classroom";

export const classroomRoutes: Routes = [
    {
        path: 'classrooms',
        component: Classrooms
    },
    {
        path: 'create-classroom',
        component: CreateClassroom
    }
];