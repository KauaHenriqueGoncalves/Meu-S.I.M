import { Routes } from "@angular/router";
import { Collaborators } from "./pages/collaborators/collaborators";
import { CreateCollaborator } from "./pages/create-collaborator/create-collaborator";

export const collaboratorRoutes: Routes = [
    {
        path: 'collaborators',
        component: Collaborators
    },
    {
        path: 'create-collaborator',
        component: CreateCollaborator
    }
];