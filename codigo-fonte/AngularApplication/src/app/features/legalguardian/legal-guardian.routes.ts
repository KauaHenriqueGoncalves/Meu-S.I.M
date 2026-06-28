import { Routes } from "@angular/router";
import { LegalGuardians } from "./pages/legal-guardians/legal-guardians";
import { CreateLegalGuardian } from "./pages/create-legal-guardian/create-legal-guardian";
import { DetailsLegalGuardian } from "./pages/details-legal-guardian/details-legal-guardian";

export const legalGuardianRoutes: Routes = [
    {
        path: 'legal-guardians',
        component: LegalGuardians
    },
    {
        path: 'create-legal-guardian',
        component: CreateLegalGuardian
    },
    {
        path: 'details-legal-guardian',
        component: DetailsLegalGuardian
    }
];