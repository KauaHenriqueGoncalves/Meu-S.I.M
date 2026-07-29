import { environment } from "../../../environments/environment";

export class ApiConfig {
    static get baseUrl(): string {
        return environment.api.baseUrl;
    }

    static get version(): string {
        return environment.api.version;
    }

    static get apiUrl(): string {
        return `${this.baseUrl}/${this.version}`;
    }

    static endpoints = {
        auth: {
            login: '/auth/login',
            loginAdmin: '/auth/login/admin',
            refresh: '/auth/refresh',
            logout: '/auth/logout',
        },
        billingdiscount: {
            base: '/billing-discounts',
            toClient: '/billing-discounts/to-client',
        },
        classroom: {
            base: '/classrooms',
            byStudent: '/classrooms/student',
            addStudent: '/classrooms/{id}/add-student',
            removeStudent: '/classrooms/{id}/remove-student',
        },
        classType: {
            base: '/class-types'
        },
        schoolPlan: {
            base: '/school-plans',
            toClient: '/school-plans/to-client',
        },
        student: {
            base: '/students',
            legalGuardian: '/students/legal-guardian'
        },
        subject: {
            base: '/subjects'
        },
        subscription: {
            base: '/school-subscriptions',
            active: '/school-subscriptions/active',
            cancel: '/school-subscriptions/{id}/cancel',
            reative: '/school-subscriptions/{id}/reactive'
        },
        schedule: {
            base: '/classrooms/{classroomId}/schedules',
        },
        user: {
            base: '/users',
        },
        collaborator: {
            base: '/collaborators',
            changePassword: '/collaborators/{id}/password'
        },
        legalGuardian: {
            base: '/legal-guardians',
            changePassword: '/legal-guardians/{id}/password'
        },
        schoolAdmin: {
            base: '/school-admins',
        },
        school: {
            base: '/schools',
        }
    };
}