import { Component } from '@angular/core';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-public-sidebar',
  imports: [RouterLink],
  templateUrl: './public-sidebar.html',
  styleUrl: './public-sidebar.sass',
})
export class PublicSidebar {
  isMenuOpen: boolean = false;

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
    
    if (this.isMenuOpen) {
      document.body.style.overflow = 'hidden';
    } 
    else {
      document.body.style.overflow = '';
    }
  }
}
