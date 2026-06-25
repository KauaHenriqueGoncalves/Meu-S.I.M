import { Routes } from "@angular/router";
import { Collaborators } from "./pages/collaborators/collaborators";
import { CreateCollaborator } from "./pages/create-collaborator/create-collaborator";
import { DetailsCollaborator } from "./pages/details-collaborator/details-collaborator";

export const collaboratorRoutes: Routes = [
    {
        path: 'collaborators',
        component: Collaborators
    },
    {
        path: 'create-collaborator',
        component: CreateCollaborator
    },
    {
        path: 'details-collaborator',
        component: DetailsCollaborator
    }
];