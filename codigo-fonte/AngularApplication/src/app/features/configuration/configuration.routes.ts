import { Routes } from "@angular/router";
import { Configurations } from "./pages/configurations/configurations";
import { EditSchool } from "./pages/edit-school/edit-school";

export const configurationRoutes: Routes = [
    {
        path: 'configurations',
        component: Configurations,
    },
    {
        path: 'configurations/edit-school',
        component: EditSchool
    }
];