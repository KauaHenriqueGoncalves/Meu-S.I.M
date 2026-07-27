import { Routes } from "@angular/router";
import { Classrooms } from "./pages/classrooms/classrooms";
import { CreateClassroom } from "./pages/create-classroom/create-classroom";
import { DetailsClassroom } from "./pages/details-classroom/details-classroom";
import { AboutClassroom } from "./pages/about-classroom/about-classroom";
import { StudentsClassroom } from "./pages/students-classroom/students-classroom";
import { ScheduleClassroom } from "./pages/schedule-classroom/schedule-classroom";

export const classroomRoutes: Routes = [
    {
        path: 'classrooms',
        component: Classrooms
    },
    {
        path: 'create-classroom',
        component: CreateClassroom
    },
    {
        path: 'about-classroom',
        component: AboutClassroom,
        children: [
            {
                path: 'details',
                component: DetailsClassroom
            },
            {
                path: 'students',
                component: StudentsClassroom
            },
            {
                path: 'schedule',
                component: ScheduleClassroom
            }
        ]
    }
];