import { Component } from "@angular/core";

@Component({
    selector: 'app-instagram-icon',
    template: `
        <svg xmlns="http://www.w3.org/2000/svg" 
            width="20" height="20" 
            viewBox="0 0 24 24" 
            fill="none" 
            stroke="currentColor" 
            stroke-width="2" 
            stroke-linecap="round" 
            stroke-linejoin="round">

        <rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect>
        <path d="M16 11.37a4 4 0 1 1-4.63-4.63 
                4 4 0 0 1 4.63 4.63z"></path>
        <line x1="17.5" y1="6.5" x2="17.5" y2="6.5"></line>

        </svg>
    `
})
export class InstagramIcon { }




