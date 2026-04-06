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
            refresh: '/auth/refresh',
            logout: '/auth/logout'
        },
        user: {
            base: '/users'
        },
        schoolAdmin: {
            base: '/school-admins'
        },
        school: {
            base: '/schools'
        }
    };
}